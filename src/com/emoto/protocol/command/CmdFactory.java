package com.emoto.protocol.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.fields.Header;
import com.emoto.protocol.fields.Instructions;
import com.emoto.protocol.fields.Common;
import com.emoto.protocol.fields.FieldDesc;

public class CmdFactory {
	private static final int BUF_SIZE = 2048;
	private static final int CHECKSUM_START_POS = 4;
	private static Logger logger = Logger.getLogger(CmdFactory.class.getName());
	
	public static ByteBuffer encCommand(CmdBase[] commands, byte[] header) throws IllegalArgumentException, IllegalAccessException
	{
		ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
		buf.order(Common.endian);
		
		for (CmdBase cmd:commands) {
			if (cmd == null) {
				break;
			}
			buf.put(header);
			buf.mark();
			buf.putShort((short)0); //placeholder for msglen
			buf.put(cmd.getInstruction().getValue());
			
			short msgLen = 0;
			Class<?> cls = cmd.getClass();
			Field[] flds = cls.getDeclaredFields();
			for (Field f : flds)
			{
				Annotation ann = f.getDeclaredAnnotation(FieldDesc.class);
				if (ann == null) {
					continue;
				}
				int len = ((FieldDesc) ann).length();
				byte seqNum = (byte)((FieldDesc) ann).seqnum();
				Class<?> type = f.getType();
				
				buf.put(seqNum);
				buf.put((byte)len);
				if ( type == long.class )
				{
					buf.putLong( f.getLong(cmd) );
				} else if (type == int.class )
				{
					buf.putInt( f.getInt(cmd) );
				} else if (type == short.class )
				{
					buf.putShort((short)f.getShort(cmd));
				} else if (type == byte.class )
				{
					buf.put(f.getByte(cmd));
				} else if ( type == String.class)
				{
					String s = (String)f.get(cmd);
					if (s.length() < len) {
						s = String.format("%"+len+"s", s);
					}
					buf.put(s.getBytes());
				}
				
				msgLen += len + 2;
			}
			
			byte checksum = 0;
			for (int i=CHECKSUM_START_POS; i<buf.position(); i++) {
				checksum += buf.get(i);
			}
			buf.put(checksum);
			buf.put(Header.ETX);
			msgLen += 7;
			int pos = buf.position();
			buf.reset();
			buf.putShort((short)msgLen);
			buf.position(pos);
		}
		
		buf.flip();
		logger.log(Level.INFO, "Encoded command " + byte2hex(buf));
		return buf;
	}

	private static CmdBase decCommand(ByteBuffer buf, byte[] expectedHeader) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException {
		logger.log(Level.INFO, "Received command " + byte2hex(buf));
		if (buf.remaining() <= 7) {
			logger.log(Level.WARNING, "Remaininig data in buffer is too short(" + buf.remaining() + "bytes )");
			return null;
		}
		
		byte[] header = new byte[2];
		buf.get(header);
		if (!Arrays.equals(header, expectedHeader)) {
			logger.log(Level.WARNING, "Incorrect header found in incoming message");
			throw new IllegalArgumentException("Incorrect header value!");
		}
		
		short msgLen = buf.getShort();
		if (buf.remaining() < msgLen - 4) {
			logger.log(Level.WARNING, "Remaininig data in buffer is less than message length(" + msgLen + "bytes )");
			return null;
		}
		
		short calcLen = 4;
		Instructions instruction = Instructions.valueOf(buf.get());
		calcLen++;
		
		Class<?> classType = Arrays.equals(expectedHeader, Header.CLIENT_STX) ?
				ServerCmdTable.get(instruction) : ClientCmdTable.get(instruction);
		if ( classType == null) {
			logger.log(Level.WARNING, "No message type is found for instruction " + instruction);
			return null;
		}
		CmdBase command = null;
		command = (CmdBase)(classType.newInstance());
		
		//classType.getField("instruction").set(command, instruction);
		command.setInstruction(instruction.getValue());
		
		byte readData;
		Field[] flds = classType.getDeclaredFields();
		for (Field f : flds) {
			Annotation ann = f.getDeclaredAnnotation(FieldDesc.class);
			if (ann == null) {
				continue;
			}
			
			boolean checkValidity = ((FieldDesc) ann).checkValidity();
			byte seqNum = ((FieldDesc) ann).seqnum();
			readData = buf.get();
			calcLen++;
			if (checkValidity == true && readData != seqNum) {
				logger.log(Level.WARNING, "Incorrect seq number. Should be {0} but read result is {1}", new Object[] {seqNum, readData});
				return null;
			}
			
			byte fieldLen = ((FieldDesc) ann).length();
			byte readLen = buf.get();
			if (checkValidity == true && fieldLen != readLen) {
				logger.log(Level.WARNING, "Incorrecct field length. Should be {0} but read result is {1}", new Object[] {fieldLen, readLen});
				return null;
			} else {
				calcLen++;
			}
			Class<?> type = f.getType();
			if (type == long.class) {
				long data = buf.getLong();
				f.set(command, data);
			} else if (type == int.class) {
				int data = buf.getInt();
				f.set(command,  data);
			} else if (type == short.class) {
				short data = buf.getShort();
				f.set(command,  data);
			} else if (type == byte.class) {
				byte data = buf.get();
				f.set(command,  data);
			} else if (type == String.class) {
				//byte[] str = new byte[fieldLen];
				byte[] str = new byte[readLen];
				buf.get(str);
				String data = new String(str);
				f.set(command, data.trim());
			}
			calcLen += readLen;
		}
		
		byte checksum = buf.get();
		byte calcChecksum = 0;
		for (int i = CHECKSUM_START_POS; i<msgLen-2; i++) {
			calcChecksum += buf.get(i);
		}
		if (checksum != calcChecksum) {
			logger.log(Level.WARNING, "Checksum incorrect. Calculated checksum is {0} while in message checksum is {1}",
					new Object[]{calcChecksum, checksum});
			return null;
		}
		
		byte Etx = buf.get();
		if (Etx != Header.ETX) {
			logger.log(Level.WARNING, "Should be ETX but actually not");
			return null;
		}
		calcLen += 2;
		
		if (calcLen != msgLen) {
			logger.log(Level.WARNING, "Incorrecct length of message. Should be {0} but read result is {1}", new Object[] {msgLen, calcLen});
			return null;
		}
		
		return command;
	}
	
	public static Object[] decCommandAll(ByteBuffer buf, byte[] expectedHeader) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException {
		ArrayList<CmdBase> arr = new ArrayList<CmdBase>();
		buf.order(Common.endian);
		while (buf.remaining() > 0) {
			CmdBase cmd = decCommand(buf, expectedHeader);
			if (cmd != null) {
				arr.add(cmd);
			} else {
				break;
			}
		}
		return arr.toArray();
	}
	
	public static final Map<Instructions, Class<?>> ServerCmdTable = new HashMap<Instructions, Class<?>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(Instructions.CLIENT_BIKE_CONNECTED, ClientBikeConnectedReq.class);
			put(Instructions.CLIENT_EMERGENT_BUTTON_PRESSED, ClientEmergentButtonPressReq.class);
			put(Instructions.CLIENT_EMERGENT_BUTTON_RELEASEED, ClientEmergentButtonReleaseReq.class);
			put(Instructions.SERVER_START_CHARGING, ServerStartChargingResp.class);
			put(Instructions.SERVER_START_STOP_CHARGER, ServerStartStopChargerResp.class);
			put(Instructions.SERVER_STOP_CHARGING, ServerStopChargingResp.class);
			put(Instructions.CLIENT_CHARGING_STATUS, ClientChargingStatusReq.class);
			put(Instructions.CLIENT_CHARGING_STARTED, ClientConnectSucceedReq.class);
			put(Instructions.SERVER_STOP_CHARGING, ServerStopChargingResp.class);
			put(Instructions.CLIENT_CHARGING_STOPPED, ClientDisconnectSucceedReq.class);
			put(Instructions.CLIENT_BIKE_DISCONNECTED, ClientBikeDisconnectedReq.class);
			put(Instructions.CLIENT_LOGIN, ClientLoginReq.class);
			put(Instructions.CLIENT_IDLE_STATUS, ClientIdleStatusReq.class);
		}
	};
	
	public static final Map<Instructions, Class<?>> ClientCmdTable = new HashMap<Instructions, Class<?>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(Instructions.CLIENT_BIKE_CONNECTED, ClientBikeConnectedResp.class);
			put(Instructions.CLIENT_EMERGENT_BUTTON_PRESSED, ClientEmergentButtonPressResp.class);
			put(Instructions.CLIENT_EMERGENT_BUTTON_RELEASEED, ClientEmergentButtonReleaseResp.class);
			put(Instructions.SERVER_START_CHARGING, ServerStartChargingReq.class);
			put(Instructions.SERVER_START_STOP_CHARGER, ServerStartStopChargerReq.class);
			put(Instructions.SERVER_STOP_CHARGING, ServerStopChargingReq.class);
			put(Instructions.CLIENT_CHARGING_STATUS, ClientChargingStatusResp.class);
			put(Instructions.CLIENT_CHARGING_STARTED, ClientConnectSucceedResp.class);
			put(Instructions.SERVER_STOP_CHARGING, ServerStopChargingReq.class);
			put(Instructions.CLIENT_CHARGING_STOPPED, ClientDisconnectSucceedResp.class);
			put(Instructions.CLIENT_BIKE_DISCONNECTED, ClientBikeDisconnectedResp.class);
			put(Instructions.CLIENT_LOGIN, ClientLoginResp.class);
			put(Instructions.CLIENT_IDLE_STATUS, ClientIdleStatusResp.class);
		}
	};
	
	private static String byte2hex(ByteBuffer buffer){  
        StringBuffer h = new StringBuffer("");  
          
        for(int i = 0; i < buffer.limit(); i++){  
            String temp = Integer.toHexString(buffer.get(i) & 0xFF);  
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h.append(" " + temp);  
        }  
          
        return h.toString();  
          
    }  
}

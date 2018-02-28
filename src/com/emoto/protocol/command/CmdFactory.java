package com.emoto.protocol.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
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
	private static Logger logger = Logger.getLogger(CmdFactory.class.getName());
	
	public static ByteBuffer encCommand(CmdBase[] commands) throws IllegalArgumentException, IllegalAccessException
	{
		ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
		buf.order(Common.endian);
		for (CmdBase cmd:commands) {
			buf.put(Header.SERVER_STX);
			buf.mark();
			buf.putShort((short)0); //placeholder for msglen
			buf.put(cmd.getInstruction());
			
			short msgLen = 0;
			byte checksum = 0;
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
					buf.putLong( (long)f.get(cmd) );
				}
				else if ( type == String.class)
				{
					buf.put(((String)(f.get(cmd))).getBytes());
				}
				
				msgLen += len;
			}
			
			buf.put(checksum);
			buf.put(Header.ETX);
			msgLen += 7;
			buf.reset();
			buf.putShort((short)msgLen);
			
			logger.log(Level.FINE, "Generate command as " + buf.array());
		}
		
		buf.flip();
		return buf;
	}
	
	public static CmdBase decCommand(ByteBuffer buf) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException {
		if (buf.remaining() <= 7) {
			logger.log(Level.WARNING, "Remaininig data in buffer is too short(" + buf.remaining() + "bytes )");
			return null;
		}
		
		byte header[] = new byte[2];
		buf.get(header);
		if (!Arrays.equals(header, Header.SERVER_STX)) {
			logger.log(Level.WARNING, "Incorrect header {0}. Whole message is {1}", new Object[] {header, buf});
			return null;
		}
		
		short msgLen = buf.getShort();
		if (buf.remaining() < msgLen - 4) {
			logger.log(Level.WARNING, "Remaininig data in buffer is less than message length(" + msgLen + "bytes )");
			return null;
		}
		
		short calcLen = 2;
		Instructions instruction = Instructions.valueOf(buf.get());
		calcLen++;
		
		Class<?> classType = CmdTable.get(instruction);
		if ( classType == null) {
			logger.log(Level.WARNING, "No message type is found for instruction ", instruction);
			return null;
		}
		CmdBase command = null;
		command = (CmdBase)(classType.newInstance());
		
		classType.getField("instruction").set(command, instruction);
		
		byte readData;
		Field[] flds = classType.getDeclaredFields();
		for (Field f : flds) {
			Annotation ann = f.getDeclaredAnnotation(FieldDesc.class);
			if (ann == null) {
				continue;
			}
			
			byte seqNum = (byte)((FieldDesc) ann).seqnum();
			readData = buf.get();
			calcLen++;
			if (readData != seqNum) {
				logger.log(Level.WARNING, "Incorrecct seq number. Should be {0} but read result is {1}", new Object[] {seqNum, readData});
				return null;
			}
			
			int fieldLen = ((FieldDesc) ann).length();
			int readLen = buf.get();
			if (fieldLen != readLen) {
				logger.log(Level.WARNING, "Incorrecct field length. Should be {0} but read result is {1}", new Object[] {fieldLen, readLen});
				return null;
			} else {
				calcLen += readLen;
			}
			Class<?> type = f.getType();
			if (type == long.class) {
				long data = buf.getLong();
				f.set(command, data);
			} else if (type == int.class) {
				int data = buf.getInt();
				f.set(command,  data);
			} else if (type == byte.class) {
				byte data = buf.get();
				f.set(command,  data);
			} else if (type == String.class) {
				byte[] str = new byte[fieldLen];
				buf.get(str);
				String data = new String(str);
				f.set(command, data);
			}
		}
		byte checksum = buf.get();
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
	
	public static final Map<Instructions, Class<?>> CmdTable = new HashMap<Instructions, Class<?>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(Instructions.CLIENT_EMERGENT_BUTTON_PRESS, ClientEmergentButtonPressReq.class);
			put(Instructions.CLIENT_EMERGENT_BUTTON_RELEASE, ClientEmergentButtonReleaseReq.class);
			put(Instructions.SERVER_START_CHARGING_REQ, ServerStartChargingReq.class);
			put(Instructions.SERVER_START_STOP_CHARGER_REQ, ServerStartStopChargerReq.class);
			put(Instructions.SERVER_STOP_CHARGING_REQ, ServerStopChargingReq.class);
		}
	};
}

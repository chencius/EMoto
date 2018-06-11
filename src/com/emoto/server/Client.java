package com.emoto.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.emoto.protocol.command.ClientBikeConnectedReq;
import com.emoto.protocol.command.ClientBikeDisconnectedReq;
import com.emoto.protocol.command.ClientChargingStatusReq;
import com.emoto.protocol.command.ClientConnectSucceedReq;
import com.emoto.protocol.command.ClientDisconnectSucceedReq;
import com.emoto.protocol.command.ClientLoginReq;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.CmdFactory;
import com.emoto.protocol.command.ServerStartChargingReq;
import com.emoto.protocol.command.ServerStartChargingResp;
import com.emoto.protocol.command.ServerStopChargingReq;
import com.emoto.protocol.command.ServerStopChargingResp;
import com.emoto.protocol.fields.ChargeSource;
import com.emoto.protocol.fields.ChargeStatus;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.Header;

public class Client {
	private static AsynchronousSocketChannel client;
	
	public static void main(String args[]) throws Exception {
		client = AsynchronousSocketChannel.open();
		//InetSocketAddress address = new InetSocketAddress("39.108.6.246", 32111);
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
		
		Future<Void> future = client.connect(address);
		future.get();
		System.out.println("Client: connected...");
		
		//login
		CmdBase[] cmd = new CmdBase[2];
		ClientLoginReq clientLoginReq = new ClientLoginReq(-1L, "hwId1", "1.0.0", (byte)1, 1, "software",
				666, "ab");
		cmd[0] = clientLoginReq;
		cmd[1] = null;
		ByteBuffer buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		readAndPrintMsg(buffer);
		
		//to server - connected request
		ClientBikeConnectedReq clientBikeConnectedReq = new ClientBikeConnectedReq(1, (byte)1, 1, "ab");
		cmd[0] = clientBikeConnectedReq;
		cmd[1] = null;
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		readAndPrintMsg(buffer);
		
		Object[] o = readAndPrintMsg(buffer);
		ServerStartChargingReq req = (ServerStartChargingReq)o[0];
		//to server - charging request received
		cmd[0] = new ServerStartChargingResp (req.getChargeId(), req.getSessionId(),
					req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
		System.out.println("Reply command: " + cmd[0]);
		//to server - start charging successfully request
		cmd[1] = new ClientConnectSucceedReq(req.getChargeId(), req.getSessionId(),
				req.getChargePortId(), 100, ChargeSource.SERVER_CHARGING, "offline", "cardId", "batId", "ab");
		System.out.println("Reply command: " + cmd[1]);
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		readAndPrintMsg(buffer);
		
		//to server - charging status report
		cmd[0] = new ClientChargingStatusReq(1L, 1L, (byte)1, ChargeStatus.CONNECTED, ErrorCode.ACT_SUCCEDED,
				200, (byte)11, (short)12, (short)13, (byte)14, ChargeSource.SERVER_CHARGING, "", "ab");
		cmd[1] = null;
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		readAndPrintMsg(buffer);
		
		//to server - charging status report
		cmd[0] = new ClientChargingStatusReq(1L, 1L, (byte)1, ChargeStatus.CONNECTED, ErrorCode.ACT_SUCCEDED,
				202, (byte)12, (short)14, (short)15, (byte)12, ChargeSource.SERVER_CHARGING, "", "ab");
		cmd[1] = null;
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		readAndPrintMsg(buffer);
		
		//here is to receive stop request
		o = readAndPrintMsg(buffer);
		ServerStopChargingReq req1 = (ServerStopChargingReq)o[0];
		cmd[0] = new ServerStopChargingResp(req1.getChargeId(), req1.getSessionId(), req1.getChargePortId(), ErrorCode.ACT_SUCCEDED);
		cmd[1] = new ClientDisconnectSucceedReq(req1.getChargeId(), req1.getSessionId(), req1.getChargePortId(), 500, ChargeSource.SERVER_CHARGING, ErrorCode.ACT_SUCCEDED, "", "", 101, "ab");
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		readAndPrintMsg(buffer);
		
		cmd[0] = new ClientBikeDisconnectedReq(1L, (byte)1, 1, "ab");
		cmd[1] = null;
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		readAndPrintMsg(buffer);
	}
	
	private static Object[] readAndPrintMsg(ByteBuffer buffer) throws InterruptedException, ExecutionException, IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException {
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		
		Object[] obj = CmdFactory.decCommand(buffer, Header.SERVER_STX);
		for (Object o : obj) {
			CmdBase cmd = (CmdBase)o;
			System.out.println("Receive " + cmd);
		}
		return obj;
	}
}

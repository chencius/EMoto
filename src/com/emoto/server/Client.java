package com.emoto.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
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
import com.emoto.protocol.fields.Instructions;

public class Client {
	public static void main(String args[]) throws Exception {
		AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
		//InetSocketAddress address = new InetSocketAddress("39.108.6.246", 32111);
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
		
		Future<Void> future = client.connect(address);
		future.get();
		System.out.println("Client: connected...");
		
		CmdBase[] cmd = new CmdBase[1];
		ClientLoginReq clientLoginReq = new ClientLoginReq(-1L, "hwId001", "1.0.0", (byte)1, 2, "software",
				666, "abcdefgh");
		cmd[0] = clientLoginReq;
		ByteBuffer buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		Object[] objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		ClientBikeConnectedReq clientBikeConnectedReq = new ClientBikeConnectedReq(1, (byte)1, 1, "abcdefgh");
		cmd[0] = clientBikeConnectedReq;
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();		
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		CmdBase c = (CmdBase)(objs[0]);
		System.out.println("Received command: " + c);
		
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();		
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		c = (CmdBase)(objs[0]);
		
		CmdBase[] comm = new CmdBase[2];
		
		switch(c.getInstruction()) {
		case SERVER_START_CHARGING:
		{
			ServerStartChargingReq req = (ServerStartChargingReq)c;
			comm[0] = new ServerStartChargingResp (req.getChargeId(), req.getSessionId(),
					req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
			System.out.println("Reply command: " + comm[0]);
			//error
			//comm[1] = new ClientConnectSucceedReq(req.getChargeId(), req.getSessionId(),
			//		req.getChargePortId(), 100, ChargeSource.SERVER_CHARGING);
			System.out.println("Reply command: " + comm[1]);
			break;
		}
		case CLIENT_CHARGING_STARTED:
		{
			break;
		}
		default:
			break;
		}
		
//		comm[0] = new ServerStartChargingResp (1L, 1L, (byte)1, ErrorCode.ACT_SUCCEDED);
//		System.out.println("Reply command: " + comm[0]);
//		
//		comm[1] = new ClientConnectSucceedReq(1L, 1L,
//				(byte)1, 100, ChargeSource.SERVER_CHARGING);
//		System.out.println("Reply command: " + comm[1]);
		
		buffer = CmdFactory.encCommand(comm, Header.CLIENT_STX);
		client.write(buffer).get();
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		CmdBase[] cmd1 = new CmdBase[2];
		/*
		cmd1[0] = new ClientChargingStatusReq(1L, 1L, (byte)1, ChargeStatus.CONNECTED, ErrorCode.ACT_SUCCEDED,
				200, (byte)11, (short)12, (short)13, (byte)14, ChargeSource.SERVER_CHARGING);
		cmd1[1] = null;
		buffer = CmdFactory.encCommand(cmd1, Header.CLIENT_STX);
		client.write(buffer);
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		cmd1[0] = new ClientChargingStatusReq(1L, 1L, (byte)1, ChargeStatus.CONNECTED, ErrorCode.ACT_SUCCEDED,
				300, (byte)15, (short)16, (short)17, (byte)18, ChargeSource.SERVER_CHARGING);
				*/
		cmd1[1] = null;
		buffer = CmdFactory.encCommand(cmd1, Header.CLIENT_STX);
		client.write(buffer);
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		//here is to receive stop req
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		ServerStopChargingReq req = (ServerStopChargingReq)objs[0];
		cmd1[0] = new ServerStopChargingResp(req.getChargeId(), req.getSessionId(), req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
		cmd1[1] = new ClientDisconnectSucceedReq(req.getChargeId(), req.getSessionId(), req.getChargePortId(), 500, ChargeSource.SERVER_CHARGING, ErrorCode.ACT_SUCCEDED);
		buffer = CmdFactory.encCommand(cmd1, Header.CLIENT_STX);
		client.write(buffer);
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		cmd1[0] = new ClientBikeDisconnectedReq(req.getChargeId(), req.getChargePortId(), 1, "abcdefgh");
		cmd1[1] = null;
		buffer = CmdFactory.encCommand(cmd1, Header.CLIENT_STX);
		client.write(buffer);
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		objs = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
	}
}

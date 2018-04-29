package com.emoto.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

import com.emoto.protocol.command.ClientBikeConnectedReq;
import com.emoto.protocol.command.ClientChargingStatusReq;
import com.emoto.protocol.command.ClientConnectSucceedReq;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.CmdFactory;
import com.emoto.protocol.command.ServerStartChargingReq;
import com.emoto.protocol.command.ServerStartChargingResp;
import com.emoto.protocol.fields.ChargeSource;
import com.emoto.protocol.fields.ChargeStatus;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.Header;

public class Client2 {
	public static void main(String args[]) throws Exception {
		AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
		
		Future<Void> future = client.connect(address);
		future.get();
		System.out.println("Client: connected...");
		
		CmdBase[] cmd = new CmdBase[2];
		CmdBase myCmd = new ClientBikeConnectedReq(1, (byte)1, 1, "abcdefgh");
		
		cmd[0] = myCmd;
		cmd[1] = null;
		ByteBuffer buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();		
		Object[] receivedCmd = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
			
		cmd[0] = myCmd;
		cmd[1] = null;
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();		
		receivedCmd = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();		
		receivedCmd = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		myCmd = (CmdBase)(receivedCmd[0]);
		System.out.println("Received command: " + myCmd);
		
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();		
		receivedCmd = CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		myCmd = (CmdBase)(receivedCmd[0]);
		
		switch(myCmd.getInstruction()) {
		case SERVER_START_CHARGING:
		{
			ServerStartChargingReq req = (ServerStartChargingReq)myCmd;
			cmd[0] = new ServerStartChargingResp (req.getChargeId(), req.getSessionId(),
					req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
			System.out.println("Reply command: " + cmd[0]);
			//error
			//cmd[1] = new ClientConnectSucceedReq(req.getChargeId(), req.getSessionId(),
			//		req.getChargePortId(), 100, ChargeSource.SERVER_CHARGING);
			System.out.println("Reply command: " + cmd[1]);
			break;
		}
		case CLIENT_CHARGING_STARTED:
		{
			break;
		}
		default:
			break;
		}
		buffer = CmdFactory.encCommand(cmd, Header.CLIENT_STX);
		client.write(buffer);
		/*
		cmd[0] = new ClientChargingStatusReq(1L, 1L, (byte)1, ChargeStatus.CONNECTED, ErrorCode.ACT_SUCCEDED,
				200, (byte)11, (short)12, (short)13, (byte)14, ChargeSource.SERVER_CHARGING);
		client.write(buffer);
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		receivedCmd = (CmdBase[]) CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
		
		cmd[0] = new ClientChargingStatusReq(1L, 1L, (byte)1, ChargeStatus.CONNECTED, ErrorCode.ACT_SUCCEDED,
				300, (byte)15, (short)16, (short)17, (byte)18, ChargeSource.SERVER_CHARGING);
				*/
		client.write(buffer);
		buffer.clear();
		client.read(buffer).get();
		buffer.flip();
		receivedCmd = (CmdBase[]) CmdFactory.decCommandAll(buffer, Header.SERVER_STX);
	}
}

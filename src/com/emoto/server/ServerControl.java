package com.emoto.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.CmdFactory;
import com.emoto.protocol.command.ServerStartChargingReq;
import com.emoto.protocol.command.ServerStopChargingReq;
import com.emoto.protocol.fields.Header;
import com.emoto.statemachine.ChargePoint;

public class ServerControl {
	private static Server server;
	private static Logger logger = Logger.getLogger(ServerControl.class.getName());
	
	static {
		server = new Server();
		new Thread(server).start();
	}

	private void sendCmd(CmdBase cmd) throws IllegalArgumentException, IllegalAccessException {
		long chargeId = cmd.getChargeId();
		logger.log(Level.INFO, "Server send command: " + cmd);
		ChargePoint cp = server.chargePoints.get(chargeId);
		if (cp == null) {
			logger.log(Level.WARNING, "No chargePoint registered for chargeId " + chargeId);
			return;
		}
		
		AsynchronousSocketChannel channel = cp.getChannel();
		if (channel == null) {
			logger.log(Level.WARNING, "No communication channel associated to chargeId " + chargeId);
			return;
		}
		
		CmdBase[] cmds = new CmdBase[1];
		cmds[0] = cmd;
		ByteBuffer buf = CmdFactory.encCommand(cmds, Header.SERVER_STX);
		channel.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
			public void completed(Integer result, ByteBuffer buf) {
                if (buf.hasRemaining()) {
                	logger.log(Level.WARNING, "Command is not fulled sent out!");
                	channel.write(buf, buf, this);  
                }  
            }  

            public void failed(Throwable exc, ByteBuffer attachment) {  
                try {  
                	channel.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }
            }  
		});
	}
	
	public void startCharging(long chargeId, long sessionId, byte chargerPortId, String signature) throws IllegalArgumentException, IllegalAccessException {
		ServerStartChargingReq req = new ServerStartChargingReq(chargeId, sessionId, chargerPortId, signature);
		sendCmd(req);
		logger.log(Level.INFO, "Server start charge command sent to chargePoint");
	}
	
	public void stopCharging(long chargeId, long sessionId, byte chargerPortId, String signature) throws IllegalArgumentException, IllegalAccessException {
		ServerStopChargingReq req = new ServerStopChargingReq(chargeId, sessionId, chargerPortId, signature);
		sendCmd(req);
		logger.log(Level.INFO, "Server stop charge command sent to chargePoint");
	}
}

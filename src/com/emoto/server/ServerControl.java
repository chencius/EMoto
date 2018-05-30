package com.emoto.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.CmdFactory;
import com.emoto.protocol.command.IPortBasedCmd;
import com.emoto.protocol.command.ServerStartChargingReq;
import com.emoto.protocol.command.ServerStopChargingReq;
import com.emoto.protocol.fields.Header;
import com.emoto.protocol.fields.ValueReturned;
import com.emoto.statemachine.ChargePoint;

public class ServerControl {
	private static Server server;
	private static Logger logger = Logger.getLogger(ServerControl.class.getName());
	
	static {
		server = new Server();
		new Thread(server).start();
	}

	private List<ValueReturned> sendCmd(CmdBase cmd) throws IllegalArgumentException, IllegalAccessException {
		long chargeId = cmd.getChargeId();
		logger.log(Level.INFO, "Server send " + cmd);
		ChargePoint cp = server.chargePoints.get(chargeId);
		if (cp == null) {
			logger.log(Level.WARNING, "No chargePoint registered for chargeId " + chargeId);
			return null;
		}
		
		AsynchronousSocketChannel channel = cp.getChannel();
		if (channel == null) {
			logger.log(Level.WARNING, "No communication channel associated to chargeId " + chargeId);
			return null;
		}
		
		final List<ValueReturned> returned = new ArrayList<>();
		CmdBase[] cmds = new CmdBase[1];
		cmds[0] = cmd;
		ByteBuffer buf = CmdFactory.encCommand(cmds, Header.SERVER_STX);
		CountDownLatch lock = new CountDownLatch(1);
		channel.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
			public void completed(Integer result, ByteBuffer buf) {
                if (buf.hasRemaining()) {
                	logger.log(Level.WARNING, "Command is not fulled sent out. Send the remaining command!");
                	channel.write(buf, buf, this);  
                }
                if (cmd instanceof IPortBasedCmd) {
                	int portId = ((IPortBasedCmd)cmd).getChargePortId();
                	ValueReturned ret = cp.getPorts()[portId].getValueReturned();
                	if (ret != null) {
                		returned.add(ret);
                	}
                }
                lock.countDown();
            }  

            public void failed(Throwable exc, ByteBuffer attachment) {  
            	logger.log(Level.WARNING, "WebService send message to chargePoint failed");
            	try {
                	channel.close();  
                } catch (IOException e) {  
                	e.printStackTrace();
                }
            }  
		});
		
		try {
			lock.await();
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "WebService waiting for chargePoint's response failed");
			e.printStackTrace();
		}
		return returned;
	}
	
	class Data {
		public int age;
		public String name;
		public Data(int age, String name) {
			this.age = age;
			this.name = name;
		}
	}
	
	public boolean startCharging(long chargeId, long sessionId, byte chargerPortId, String signature) throws IllegalArgumentException, IllegalAccessException {
//		ServerStartChargingReq req = new ServerStartChargingReq(chargeId, sessionId, chargerPortId, signature);
//		List<ValueReturned> ret = sendCmd(req);
//		if (ret == null || ret.size() != 1) {
//			logger.log(Level.WARNING, "WebService of startCharging return value invalid");
//			return false;
//		} else {
//			logger.log(Level.INFO, "Server start charge command sent to chargePoint");
//			return ret.get(0).getStatus();
//		}
		return true;
	}
	
	public Data stopCharging(long chargeId, long sessionId, byte chargerPortId, String signature) throws IllegalArgumentException, IllegalAccessException {
		ServerStopChargingReq req = new ServerStopChargingReq(chargeId, sessionId, chargerPortId, signature);
//		List<ValueReturned> ret = sendCmd(req);
//		if (ret == null || ret.size() != 1) {
//			logger.log(Level.WARNING, "WebService of stopCharging return value invalid");
//			return false;
//		} else {
//			logger.log(Level.INFO, "Server stop charge command sent to chargePoint");
//			return ret.get(0).getStatus();
//		}
		return new Data(41, "Hanqiang");
	}
	
//	public OMElement stopCharging(long chargeId, long sessionId, byte chargerPortId, String signature) throws IllegalArgumentException, IllegalAccessException {
//		OMFactory fac = OMAbstractFactory.getOMFactory();
//		OMNamespace omNs = fac.createOMNamespace("http://example1.org/example1", "example1");
//		OMElement method = fac.createOMElement("newTestResponse", omNs);
//		OMElement value = fac.createOMElement("greeting", omNs);
//		fac.createOMText("yes, you did it!");
//		method.addChild(value);
//		return method;
//	}
}

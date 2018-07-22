package com.emoto.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.CmdFactory;
import com.emoto.protocol.command.IPortBasedCmd;
import com.emoto.protocol.command.ServerStartChargingReq;
import com.emoto.protocol.command.ServerStopChargingReq;
import com.emoto.protocol.fields.Header;
import com.emoto.server.Server.HWMapping;
import com.emoto.statemachine.ChargePoint;
import com.emoto.statemachine.ChargePort;
import com.emoto.websocket.ChargingStatus;
import com.emoto.websocket.IValueReturned;
import com.emoto.websocket.ValueReturned;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@ServerEndpoint("/EMotoEntry")
public class ServerControl {
	private static Server server;
	private static Map<Long, Session> clients = new HashMap<>();
	private static Logger logger = Logger.getLogger(ServerControl.class.getName());
	
	private static Callback cb = new Callback();
	
	static {
		server = new Server();
		server.setCallback(cb);
		Thread dbThread = new Thread(server);
		dbThread.setName("dbThread");
		dbThread.setDaemon(true);
		dbThread.start();
		System.out.println("ServerControl init done!!!");
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Open Connection " + session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("Close Connection ...");
	}
	
	@OnMessage
	public String onMessage(String message, Session session) throws IllegalArgumentException, IllegalAccessException {
		System.out.println("Message " + message + " received from : " + session.getId());

		CmdBase req = null;
		IValueReturned ret = null;
		
		JSONObject data = (JSONObject) JSONSerializer.toJSON(message);
		String func = (String) data.get("func");
		String hwId = data.getString("hwId");
		HWMapping hwMap = server.hwId2ChargePoint.get(hwId);
		if (hwMap == null) {
			logger.log(Level.WARNING, "No chargePoint found with hwId: " + hwId);
			ret = new ValueReturned();
			ret.setReason("No chargePoint found with hwId: " + hwId);
			JSONObject jsonObj = JSONObject.fromObject(ret);
			return jsonObj.toString();
		}
		
		long chargeId = hwMap.chargeId;
		clients.put(chargeId, session);
		
		byte portId = (byte) data.getInt("portId");
		String signature = "";
		ChargePoint cp = server.chargePoints.get(chargeId);
		if (cp == null) {
			logger.log(Level.WARNING, "No chargePoint registered for chargeId: " + chargeId);
			ret = new ValueReturned();
			ret.setReason("No chargePoint found with hwId: " + hwId);
			JSONObject jsonObj = JSONObject.fromObject(ret);
			return jsonObj.toString();
		}

		long sessionId = server.sessionId.incrementAndGet();
		ChargePort[] ports = cp.getPorts();
		ports[portId-1].setValueReturned(null);
		ports[portId-1].setSessionId(sessionId);
		ports[portId-1].setLock(new CountDownLatch(1));
		
		switch(func) {
		case "startCharging":
			req = new ServerStartChargingReq(chargeId, sessionId, portId, signature);
			break;
		case "stopCharging":
			req = new ServerStopChargingReq(chargeId, sessionId, portId, signature);
			break;
		case "getChargingStatus":
			break;
		default:
			break;
		}
		
		if (req != null) {
			ret = sendCmd(req);
		} else {
			logger.log(Level.WARNING, "Invalid request: " + req);
			ret = new ValueReturned();
			ret.setReason("Invalid request received");
		}
		if (ret == null) {
			logger.log(Level.WARNING, "No valid return with request: " + req);
			ret = new ValueReturned();
			ret.setReason("No valid return");
		}
		logger.log(Level.INFO, "WebSocket gets returned value: " + ret);
		ChargingStatus feedback = new ChargingStatus("feedback", ret.getChargeId(), ret.getPortId(), ret.getStatus(),
				ret.getMeterValue(), ret.getVoltage(), ret.getCurrency(), ret.getEstimatedTime());
		JSONObject jsonObj = JSONObject.fromObject(feedback);
		System.out.println("onMessage toString: " + jsonObj);
		return jsonObj.toString();
	}
	
	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}
	private IValueReturned sendCmd(CmdBase cmd) throws IllegalArgumentException, IllegalAccessException {
		logger.log(Level.INFO, "Server send " + cmd);
		
		long chargeId = cmd.getChargeId();
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
		
		CmdBase[] cmds = new CmdBase[1];
		cmds[0] = cmd;
		ByteBuffer buf = CmdFactory.encCommand(cmds, Header.SERVER_STX);
		
		channel.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
			public void completed(Integer result, ByteBuffer buf) {
                if (buf.hasRemaining()) {
                	logger.log(Level.WARNING, "Command is not fulled sent out. Send the remaining command!");
                	channel.write(buf, buf, this);  
                }
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
		
		IValueReturned ret = null;
		if (cmd instanceof IPortBasedCmd) {
        	int portId = ((IPortBasedCmd)cmd).getChargePortId();
        	ChargePort[] ports = cp.getPorts();
        	
        	try {
        		CountDownLatch lock = ports[portId-1].getLock();
        		if (lock != null) {
        			lock.await();
        		}
        		lock = null;
    		} catch (InterruptedException e) {
    			logger.log(Level.WARNING, "WebService waiting for chargePoint's response interrupted");
    			e.printStackTrace();
    		}
        	
        	ret = cp.getPorts()[portId-1].getValueReturned();
        }
		return ret;
	}
	
	public static class Callback {
		public void report(IValueReturned ret) {
			long chargeId = ret.getChargeId();
			Session session = clients.get(chargeId);
			if (session == null) {
				logger.log(Level.WARNING, "No session associated with chargeId: " + chargeId);
				return;
			}
			
			ChargingStatus feedback = new ChargingStatus("feedback", ret.getChargeId(), ret.getPortId(), ret.getStatus(),
					ret.getMeterValue(), ret.getVoltage(), ret.getCurrency(), ret.getEstimatedTime());
			JSONObject jsonObj = JSONObject.fromObject(feedback);
			try {
				session.getBasicRemote().sendText(jsonObj.toString());
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to send json Ojbect: " + jsonObj);
				e.printStackTrace();
			}
		}
	}
}

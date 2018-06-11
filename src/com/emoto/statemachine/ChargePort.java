package com.emoto.statemachine;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientIdleStatusReq;
import com.emoto.protocol.command.ClientIdleStatusResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.server.Server;
import com.emoto.websocket.IValueReturned;

public class ChargePort {
	public State onlineState;
	public State connectedState;
	public State chargingState;
	private State state;
	private Server server;
	
	private volatile long sessionId;
	
	private long chargeId;
	private byte portId;
	
	private CountDownLatch lock;
	
	private Timer timer;
	private final int SECONDS = 700;
	private TimerTask timerTask;
	
	private boolean active;
	private Object callback;
	
	private IValueReturned valueReturned;
	
	private static Logger logger = Logger.getLogger(ChargePort.class.getName());
	
	public ChargePort(Server server, long chargeId, byte portId) {
		this.server = server;
		this.onlineState = new Online(this);
		this.connectedState = new Connected(this);
		this.chargingState = new Charging(this);
		this.state = null;
		this.active = true;
		
		this.chargeId = chargeId;
		this.portId = portId;
		
		this.lock = null;
		this.valueReturned = null;
		
		this.timer = new Timer();
		this.timerTask = new ChargePointTimerTask();
	}

	public long getChargeId() {
		return chargeId;
	}

	public byte getPortId() {
		return portId;
	}

	public Server getServer() {
		return server;
	}
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public IValueReturned getValueReturned() {
		return this.valueReturned;
	}
	
	public void setValueReturned(IValueReturned value) {
		this.valueReturned = value;
	}
	
	public CountDownLatch getLock() {
		return lock;
	}
	
	public void setLock(CountDownLatch lock) {
		this.lock = lock;
	}
	
	public CmdBase[] execCmd(CmdBase cmd) {
		logger.log(Level.INFO, "Execute {0} under state of {1}", new Object[]{cmd, this.state});
		
		if (!getActive()) {
			logger.log(Level.WARNING, "Receive {0} while port is inActive", cmd);
			return null;
		}
		
		switch(cmd.getInstruction()) {
		case CLIENT_IDLE_STATUS:
		{
			ClientIdleStatusReq req = (ClientIdleStatusReq)cmd;
			if (state != this.onlineState) {
				logger.log(Level.WARNING, "Receive {0} but chargeId {1} portId {2} is not idle",
						new Object[]{req, req.getChargeId(), req.getChargePortId()});
				return null;
			}
			
			if (req.getChargeId() == this.chargeId &&
				req.getChargePortId() == this.portId) {
				CmdBase[] resp = new CmdBase[1];
				resp[0] = new ClientIdleStatusResp(req.getChargeId(), ErrorCode.ACT_SUCCEDED);
				restartTimer();

				return resp;
			} else {
				logger.log(Level.WARNING, "Receive {0} by the wrong chargePoint {1} portId {2}",
						new Object[]{req, req.getChargeId(), req.getChargePortId()});
				return null;
			}
		
		}
		default:
			break;
		}
		return state.execCmd(cmd);
	}
	
	public long getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	
	public boolean getActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public String toString() {
		return "chargeId " + this.chargeId + " portId " + this.portId;
	}

	public void restartTimer() {
		timer.cancel();
		timer = new Timer();
		timer.schedule(timerTask, SECONDS*1000);
	}
	
	private class ChargePointTimerTask extends TimerTask {
		@Override
		public void run() {
			//server.chargePoints.remove(chargeId);
			logger.log(Level.WARNING, "ChargePort hasn't received ClientIdleStatus for {0} seconds. Bring it offline",
					SECONDS);
			active = false;
		}
	}

	public void setCallback(Object callback) {
		this.callback = callback;
	}
	
	public Object getCallback() {
		return callback;
	}
}

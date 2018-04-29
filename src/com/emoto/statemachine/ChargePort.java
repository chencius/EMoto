package com.emoto.statemachine;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientIdleStatusReq;
import com.emoto.protocol.command.ClientIdleStatusResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.server.Server;

public class ChargePort {
	public State onlineState;
	public State connectedState;
	public State chargingState;
	private State state;
	private Server server;
	
	private long sessionId;
	private int localId;
	
	//private Timer timer;
	private final int SECONDS = 700;
	//private TimerTask timerTask;
	
	private static Logger logger = Logger.getLogger(ChargePort.class.getName());
	
	public ChargePort(Server server) {
		this.server = server;
		this.onlineState = new Online(this);
		this.connectedState = new Connected(this);
		this.chargingState = new Charging(this);
		this.state = this.onlineState;
		
		/*
		this.timer = new Timer();
		this.timerTask = new ChargePointTimerTask();
		*/
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
	
	public CmdBase[] execCmd(CmdBase cmd) {
		logger.log(Level.INFO, "Execute command {0} under current state of {1}", new Object[]{cmd, this});
		
		switch(cmd.getInstruction()) {
		case CLIENT_IDLE_STATUS:
		{
			ClientIdleStatusReq req = (ClientIdleStatusReq)cmd;
			//if (req.getChargeId() == this.chargeId && 
			if (server.chargePoints.containsKey(req.getChargeId())) {
				CmdBase[] resp = new CmdBase[1];
				resp[0] = new ClientIdleStatusResp(req.getChargeId(), ErrorCode.ACT_SUCCEDED);
				//restartTimer();
				return resp;
			} else {
				logger.log(Level.WARNING, "Receive " + cmd + " by the wrong chargePoint" );
				return null;
			}
		
		}
		}
		return state.execCmd(cmd);
	}
	
	public long getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	
	public int getLocalId() {
		return localId;
	}
	
	public void setLocalId(int localId) {
		this.localId = localId;
	}
	
	@Override
	public String toString() {
		return state.getClass().getSimpleName();
	}
	/*
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
					new Object[]{SECONDS});
		}
	}
	*/
}

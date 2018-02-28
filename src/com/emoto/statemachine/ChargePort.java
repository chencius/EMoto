package com.emoto.statemachine;

import com.emoto.protocol.command.CmdBase;
import com.emoto.server.Server;

public class ChargePort {
	public State idleState;
	public State connectedState;
	public State readyToChargeState;
	public State chargingState;
	private State state;
	private Server server;
	
	public ChargePort(Server server) {
		this.server = server;
		this.idleState = new Idle(this);
		this.connectedState = new Connected(this);
		this.readyToChargeState = new ReadyToCharge(this);
		this.chargingState = new Charging(this);
		this.state = this.idleState;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public CmdBase[] execCmd(CmdBase cmd) {
		return state.execCmd(cmd, server);
	}
}

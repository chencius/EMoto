package com.emoto.statemachine;

import com.emoto.protocol.command.CmdBase;
import com.emoto.server.Server;

public abstract class State {
	public abstract CmdBase[] execCmd(CmdBase cmd);
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}

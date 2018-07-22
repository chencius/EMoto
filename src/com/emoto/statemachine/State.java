package com.emoto.statemachine;

import com.emoto.protocol.command.CmdBase;

public abstract class State {
	public abstract CmdBase[] execCmd(CmdBase cmd);
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}

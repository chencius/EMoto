package com.emoto.statemachine;

import com.emoto.protocol.command.CmdBase;
import com.emoto.server.Server;

public abstract class State {
	protected long sessionId;
	
	public void setSessionId(long id) {
		this.sessionId = id;
	}
	
	public CmdBase[] execCmd(CmdBase cmd, Server server);
}

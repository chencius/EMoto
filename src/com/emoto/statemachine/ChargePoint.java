package com.emoto.statemachine;

import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.IPortBasedCmd;
import com.emoto.server.Server;

public class ChargePoint {
	private final int PORT_NUM = 4;
	private ChargePort[] ports;
	private Server server;
	
	public ChargePoint(Server server) {
		this.server = server;
		ports = new ChargePort[PORT_NUM];
		for (int i=0; i<ports.length; i++) {
			ports[i] = new ChargePort(server);
		}
	}
	
	public CmdBase[] execCmd(CmdBase cmd) {
		if (cmd instanceof IPortBasedCmd) {
			byte portId = ((IPortBasedCmd)cmd).getChargerPortId();
			return ports[portId].execCmd(cmd);
		}
		return null;
	}
}

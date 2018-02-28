package com.emoto.statemachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientChargingStatusReq;
import com.emoto.protocol.command.ClientChargingStatusResp;
import com.emoto.protocol.command.ClientDisconnectSucceedReq;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.Instructions;
import com.emoto.server.Server;

public class Charging extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(Charging.class.getName());
	
	public Charging(ChargePort cp) {
		this.cp = cp;
	}

	@Override
	public CmdBase[] execCmd(CmdBase cmd, Server server) {
		switch(Instructions.valueOf(cmd.getInstruction())) {
		case CLIENT_CHARGING_STATUS:
		{
			ClientChargingStatusReq req = (ClientChargingStatusReq)cmd;
			CmdBase[] resp = new CmdBase[1];
			resp[0] = new ClientChargingStatusResp(
					req.getChargerId(), req.getSessionId(), req.getChargerPortId(), ErrorCode.ACT_SUCCEDED.getValue());
			return resp;
		}
		case CLIENT_DISCONNECTION_SUCC:
		{
			ClientDisconnectSucceedReq req = (ClientDisconnectSucceedReq)cmd;
			CmdBase[] resp = new CmdBase[1];
			resp[0] = new ClientChargingStatusResp(
					req.getChargerId(), req.getSessionId(), req.getChargerPortId(), ErrorCode.ACT_SUCCEDED.getValue());
			cp.setState(cp.idleState);
			return resp;
		}
		default:
			logger.log(Level.WARNING, "Invalid action " + cmd.getInstruction() + " with CP state = Charging");
			return null;
		}
	}
}

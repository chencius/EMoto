package com.emoto.statemachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientBikeConnectedReq;
import com.emoto.protocol.command.ClientBikeConnectedResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.ServerStartChargingReq;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.Instructions;
import com.emoto.server.Server;

public class Idle extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(Idle.class.getName());
	
	public Idle(ChargePort cp) {
		this.cp = cp;
	}

	@Override
	public CmdBase[] execCmd(CmdBase cmd, Server server) {
		switch(Instructions.valueOf(cmd.getInstruction())) {
		case CLIENT_BIKE_CONNECTTED:
			ClientBikeConnectedReq req = (ClientBikeConnectedReq)cmd;
			
			CmdBase[] resp = new CmdBase[2];
			resp[0] = new ClientBikeConnectedResp(
					req.getChargerId(), req.getChargerPortId(), req.getLocalId(), ErrorCode.ACT_SUCCEDED);
			String signature = null;
			resp[1] = new ServerStartChargingReq(
					req.getChargerId(), ++server.sessionId, req.getChargerPortId(), signature);
			
			cp.connectedState.setSessionId(server.sessionId);
			cp.setState(cp.connectedState);
			return resp;
		default:
			logger.log(Level.WARNING, "Invalid action " + cmd.getInstruction() + " with CP state = Idle");
			return null;
		}
	}
}

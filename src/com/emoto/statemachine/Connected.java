package com.emoto.statemachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.ServerStartChargingResp;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.Instructions;
import com.emoto.server.Server;

public class Connected extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(Connected.class.getName());
	
	public Connected(ChargePort cp) {
		this.cp = cp;
	}
	
	@Override
	public CmdBase[] execCmd(CmdBase cmd, Server server) {
		switch(Instructions.valueOf(cmd.getInstruction())) {
		case SERVER_START_CHARGING_REQ:
			ServerStartChargingResp resp = (ServerStartChargingResp)cmd;
			if (resp.getSessionId() == sessionId ) {
				if (resp.getErrorCode() == ErrorCode.ACT_SUCCEDED.getValue())
				{
					cp.readyToChargeState.setSessionId(sessionId);
					cp.setState(cp.readyToChargeState);
				} else {
					logger.log(Level.WARNING, "Get status " + resp.getErrorCode() + " with CP state = Connected");
				}
			} else {
				logger.log(Level.WARNING, "Current sessionId = " + sessionId + ".But received sessionId = " + resp.getSessionId());
			}
			return null;
		default:
			logger.log(Level.WARNING, "Invalid action " + cmd.getInstruction() + " with CP state = Connected");
			return null;
		}
	}
}

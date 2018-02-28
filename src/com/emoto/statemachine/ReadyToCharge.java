package com.emoto.statemachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientChargingFault;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.ClientConnectSucceedReq;
import com.emoto.protocol.command.ClientConnectSucceedResp;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.Instructions;
import com.emoto.server.Server;

public class ReadyToCharge extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(ReadyToCharge.class.getName());
	
	public ReadyToCharge(ChargePort cp) {
		this.cp = cp;
	}

	@Override
	public CmdBase[] execCmd(CmdBase cmd, Server server) {
		switch(Instructions.valueOf(cmd.getInstruction())) {
		case CLIENT_CONNECTION_SUCC:
		{
			ClientConnectSucceedReq req = (ClientConnectSucceedReq)cmd;
			
			CmdBase[] resp = null;
			if (req.getSessionId() == sessionId) {
				resp = new CmdBase[1];
				resp[0] = new ClientConnectSucceedResp(
						req.getChargerId(), req.getChargerPortId(), ErrorCode.ACT_SUCCEDED.getValue());
				cp.setState(cp.chargingState);
			} else {
				logger.log(Level.WARNING, "Current sessionId = " + sessionId + ".But received sessionId = " + req.getSessionId());
			}
			return resp;
		}
		case CLIENT_CHARGING_FAULT:
		{
			ClientChargingFault req = (ClientChargingFault)cmd;
			//suppose to return message to CP. But what errorCode to assign??
			return null;
		}	
		default:
			logger.log(Level.WARNING, "Invalid action " + cmd.getInstruction() + " with CP state = ReadyToCharge");
			return null;
		}
	}
}

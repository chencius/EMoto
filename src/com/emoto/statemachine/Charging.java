package com.emoto.statemachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientChargingStatusReq;
import com.emoto.protocol.command.ClientChargingStatusResp;
import com.emoto.protocol.command.ClientDisconnectSucceedReq;
import com.emoto.protocol.command.ClientDisconnectSucceedResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.ServerStopChargingResp;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.server.Server;

public class Charging extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(Charging.class.getName());
	
	public Charging(ChargePort cp) {
		this.cp = cp;
	}

	@Override
	public CmdBase[] execCmd(CmdBase cmd) {
		switch(cmd.getInstruction()) {
		case CLIENT_CHARGING_STATUS:
		{
			ClientChargingStatusReq req = (ClientChargingStatusReq)cmd;
			CmdBase[] resp = new CmdBase[1];
			resp[0] = new ClientChargingStatusResp(
					req.getChargeId(), req.getSessionId(), req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
			return resp;
		}
		case CLIENT_CHARGING_STOPPED:
		{
			ClientDisconnectSucceedReq req = (ClientDisconnectSucceedReq)cmd;
			CmdBase[] resp = new CmdBase[1];
			resp[0] = new ClientDisconnectSucceedResp(
					req.getChargeId(), req.getSessionId(), req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
			logger.log(Level.INFO, "Transit from state {0} to state {1}", new Object[]{this, cp.connectedState});
			cp.setState(cp.connectedState);
			return resp;
		}
		case SERVER_STOP_CHARGING:
		{
			ServerStopChargingResp resp = (ServerStopChargingResp)cmd;
			if (resp.getErrorCode() == ErrorCode.ACT_SUCCEDED &&
					resp.getSessionId() == cp.getSessionId()) {
				logger.log(Level.INFO, "Received " + resp);
			} else {
				logger.log(Level.WARNING, "Incorrect ServerStopChargingResp received for chargeId " + resp.getChargeId());
			}
			return null;
		}
		default:
			logger.log(Level.WARNING, "Failed to execute command {0} under state {1}", new Object[]{cmd, this});
			return null;
		}
	}
}

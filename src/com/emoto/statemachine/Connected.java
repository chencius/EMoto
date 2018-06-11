package com.emoto.statemachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientBikeDisconnectedReq;
import com.emoto.protocol.command.ClientBikeDisconnectedResp;
import com.emoto.protocol.command.ClientChargingFaultReq;
import com.emoto.protocol.command.ClientConnectSucceedReq;
import com.emoto.protocol.command.ClientConnectSucceedResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.ServerStartChargingResp;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.server.Server;
import com.emoto.websocket.IValueReturned;
import com.emoto.websocket.ValueReturned;

public class Connected extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(Connected.class.getName());
	
	public Connected(ChargePort cp) {
		this.cp = cp;
	}
	
	@Override
	public CmdBase[] execCmd(CmdBase cmd) {
		switch(cmd.getInstruction()) {
		case SERVER_START_CHARGING:
		{
			IValueReturned result = new ValueReturned();
			result.setChargeId(cp.getChargeId());
			result.setPortId(cp.getPortId());
			
			ServerStartChargingResp resp = (ServerStartChargingResp)cmd;
			if (resp.getSessionId() == cp.getSessionId() ) {
				if (resp.getErrorCode() == ErrorCode.ACT_SUCCEDED)
				{
					result.setStatus(true);
				} else {
					result.setStatus(false);
					result.setReason("Get status " + resp.getErrorCode() + " under state " + this);
					logger.log(Level.WARNING, "Get status " + resp.getErrorCode() + " under state " + this);
				}
			} else {
				result.setStatus(false);
				logger.log(Level.WARNING, "Current sessionId = " + cp.getSessionId() + ".But received sessionId = " + resp.getSessionId());
				result.setReason("Current sessionId = " + cp.getSessionId() + ".But received sessionId = " + resp.getSessionId());
			}
			cp.setValueReturned(result);
			return null;
		}
		case CLIENT_CHARGING_STARTED:
		{
			IValueReturned result = new ValueReturned();
			ClientConnectSucceedReq req = (ClientConnectSucceedReq)cmd;
			
			CmdBase[] resp = null;
			if (req.getSessionId() == cp.getSessionId()) {
				resp = new CmdBase[1];
				resp[0] = new ClientConnectSucceedResp(
						req.getChargeId(), req.getSessionId(), req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
				logger.log(Level.INFO, "Transit from state {0} to state {1}", new Object[]{this, cp.chargingState});
				result.setStatus(true);
				cp.setState(cp.chargingState);
			} else {
				logger.log(Level.WARNING, "Current sessionId = " + cp.getSessionId() + ".But received sessionId = " + req.getSessionId());
				result.setStatus(true);
				result.setReason("Current sessionId = " + cp.getSessionId() + ".But received sessionId = " + req.getSessionId());
			}
			cp.setValueReturned(result);
			cp.getLock().countDown();
			return resp;
		}
		case CLIENT_CHARGING_FAULT:
		{
			ClientChargingFaultReq req = (ClientChargingFaultReq)cmd;
			//suppose to return message to CP. But what errorCode to assign??
			return null;
		}
		case CLIENT_BIKE_DISCONNECTED:
		{
			ClientBikeDisconnectedReq req = (ClientBikeDisconnectedReq)cmd;
			
			CmdBase[] resp = null;
			//if (req.getLocalId() == cp.getLocalId()) {
				resp = new CmdBase[1];
				resp[0] = new ClientBikeDisconnectedResp(
						req.getChargeId(), req.getChargePortId(), req.getLocalId(), ErrorCode.ACT_SUCCEDED);
				logger.log(Level.INFO, "Transit from state {0} to state {1}", new Object[]{this, cp.onlineState});
				cp.setState(cp.onlineState);
			//} else {
			//	logger.log(Level.WARNING, "Current localId = " + cp.getLocalId() + ". But received localId = " + req.getLocalId());
			//}
			return resp;
		}
		default:
			logger.log(Level.WARNING, "Failed to execute {0} under state {1}", new Object[]{cmd, this});
			return null;
		}
	}
}

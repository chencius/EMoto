package com.emoto.statemachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientBikeConnectedReq;
import com.emoto.protocol.command.ClientBikeConnectedResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.fields.ErrorCode;

public class Online extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(Online.class.getName());
	
	public Online(ChargePort cp) {
		this.cp = cp;
	}

	@Override
	public CmdBase[] execCmd(CmdBase cmd) {
		switch(cmd.getInstruction()) {
		case CLIENT_BIKE_CONNECTED:
		{
			ClientBikeConnectedReq req = (ClientBikeConnectedReq)cmd;
			
			CmdBase[] resp = new CmdBase[1];
			resp[0] = new ClientBikeConnectedResp(
					req.getChargeId(), req.getChargePortId(), req.getLocalId(), ErrorCode.ACT_SUCCEDED);
			
			long sessionId = cp.getServer().sessionId.incrementAndGet();
			cp.setSessionId(sessionId);
			cp.setLocalId(req.getLocalId());
			cp.setState(cp.connectedState);
			return resp;
		}
		default:
		{
			logger.log(Level.WARNING, "Failed to execute {0} under state {1}", new Object[]{cmd, this});
			return null;
		}
		}
	}
}

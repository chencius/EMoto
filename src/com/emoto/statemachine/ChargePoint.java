package com.emoto.statemachine;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientIdleStatusReq;
import com.emoto.protocol.command.ClientIdleStatusResp;
import com.emoto.protocol.command.ClientLoginReq;
import com.emoto.protocol.command.ClientLoginResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.IPortBasedCmd;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.server.Server;
import com.emoto.server.Server.HWMapping;

public class ChargePoint {
	private final int PORT_NUM = 6;
	private ChargePort[] ports;
	private Server server;
	private long chargeId;
	private AsynchronousSocketChannel channel;
	private static Logger logger = Logger.getLogger(ChargePoint.class.getName());
	
	public ChargePoint(Server server, AsynchronousSocketChannel channel, long chargeId) {
		this.server = server;
		this.channel = channel;
		this.chargeId = chargeId;
		ports = new ChargePort[PORT_NUM];
		for (int i=0; i<ports.length; i++) {
			ports[i] = new ChargePort(server);
		}
	}
	
	public AsynchronousSocketChannel getChannel() {
		return this.channel;
	}
	
	public CmdBase[] execCmd(CmdBase cmd) {
		if (cmd instanceof IPortBasedCmd) {
			byte portId = ((IPortBasedCmd)cmd).getChargePortId();
			logger.log(Level.INFO, "chargePoint {0} port {1} is about to execute command: {2}",
					new Object[]{cmd.getChargeId(), portId, cmd});
			return ports[portId-1].execCmd(cmd);
		} else {
			logger.log(Level.INFO, "chargePoint {0} is about to execute command: {1}",
					new Object[]{cmd.getChargeId(), cmd});
			switch(cmd.getInstruction()) {
			case CLIENT_LOGIN:
			{
				ClientLoginReq req = (ClientLoginReq)cmd;
				CmdBase[] resp = new CmdBase[1];
				HWMapping chargePointInfo = server.barcode2ChargePoint.get(req.getHwId());
				if (chargePointInfo != null) {
					resp[0] = new ClientLoginResp(chargePointInfo.chargeId, req.getLocalId(), ErrorCode.ACT_SUCCEDED);
					for (int i = 0; i<ports.length; i++) {
						ports[i].setState(ports[i].onlineState);
						//ports[i].restartTimer();
					}
				} else {
					resp[0] = new ClientLoginResp(-1L, req.getLocalId(), ErrorCode.CHARGE_ID_ERR);
					logger.log(Level.WARNING, "Receive {0} but hwId {1} is not present", new Object[]{cmd, req.getHwId()});
				}
				return resp;
			}
			default:
				logger.log(Level.WARNING, "Failed to execute command " + cmd + " by chargePoint");
				return null;
			}
		}
	}
}

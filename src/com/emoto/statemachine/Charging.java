package com.emoto.statemachine;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientChargingStatusReq;
import com.emoto.protocol.command.ClientChargingStatusResp;
import com.emoto.protocol.command.ClientDisconnectSucceedReq;
import com.emoto.protocol.command.ClientDisconnectSucceedResp;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.ServerStopChargingResp;
import com.emoto.protocol.fields.ChargeStatus;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.server.ServerControl;
import com.emoto.websocket.IValueReturned;
import com.emoto.websocket.ValueReturned;

public class Charging extends State {
	private ChargePort cp;
	private static Logger logger = Logger.getLogger(Charging.class.getName());
	
	public Charging(ChargePort cp) {
		this.cp = cp;
	}

	private void updateChargeRecord(ClientChargingStatusReq status) {
		cp.chargeRecord.setMeter(status.getMeterValue());
		cp.chargeRecord.setVoltage(status.getChargingVoltage());
		cp.chargeRecord.setCurrency(status.getChargingCurrency());
		cp.chargeRecord.setSessionId(status.getSessionId());
	}
	
	private void completeChargeRecord() {
		cp.chargeRecord.setEndTime(new Timestamp(System.currentTimeMillis()));
		cp.getServer().recordFactory.addElement(cp.chargeRecord);
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
			
			updateChargeRecord(req);
			
			IValueReturned result = new ValueReturned();
			result.setChargeId(cp.getChargeId());
			result.setPortId(cp.getPortId());
			result.setStatus(req.getStatus().equals(ChargeStatus.CHARGING));
			result.setMeterValue(req.getMeterValue());
			result.setBatteryEnergy(req.getBatteryEnergy());
			result.setVoltage(req.getChargingVoltage());
			result.setCurrency(req.getChargingCurrency());
			result.setEstimatedTime(req.getEstimatedFinishTime());
			logger.log(Level.INFO, "CLIENT_CHARGING_STATUS: " + result );
			ServerControl.Callback cb = (ServerControl.Callback)(cp.getCallback());
			cb.report(result);
			return resp;
		}
		case CLIENT_CHARGING_STOPPED:
		{
			IValueReturned result = new ValueReturned();
			ClientDisconnectSucceedReq req = (ClientDisconnectSucceedReq)cmd;
			
			CmdBase[] resp = new CmdBase[1];
			resp[0] = new ClientDisconnectSucceedResp(
					req.getChargeId(), req.getSessionId(), req.getChargePortId(), ErrorCode.ACT_SUCCEDED);
			logger.log(Level.INFO, "Transit from state {0} to state {1}", new Object[]{this, cp.connectedState});
			cp.setState(cp.connectedState);
			
			completeChargeRecord();
			
			result.setStatus(true);
			cp.setValueReturned(result);
			cp.getLock().countDown();
			return resp;
		}
		case SERVER_STOP_CHARGING:
		{
			IValueReturned result = new ValueReturned();
			ServerStopChargingResp resp = (ServerStopChargingResp)cmd;

			if (resp.getErrorCode() == ErrorCode.ACT_SUCCEDED) {
				result.setStatus(true);
				logger.log(Level.INFO, "Received " + resp);
			} else {
				result.setStatus(false);
				logger.log(Level.WARNING, "Incorrect ServerStopChargingResp received for chargeId " + resp.getChargeId());
			}
			cp.setValueReturned(result);
			return null;
		}
		default:
			logger.log(Level.WARNING, "Failed to execute {0} under state {1}", new Object[]{cmd, this});
			return null;
		}
	}
}

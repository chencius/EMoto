package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientChargingStatusReq extends CmdBase implements IPortBasedCmd, ISessionBasedCmd {
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargerPortId;
	
	@FieldDesc(length=1, seqnum=0x04)
	protected byte status;
	
	@FieldDesc(length=1, seqnum=0x05)
	protected byte errorCode;
	
	@FieldDesc(length=4, seqnum=0x06)
	protected int meterValue;
	
	@FieldDesc(length=1, seqnum=0x07)
	protected byte batteryEnergy;
	
	@FieldDesc(length=2, seqnum=0x08)
	protected short chargingVoltage;
	
	@FieldDesc(length=2, seqnum=0x09)
	protected short chargingCurrency;
	
	@FieldDesc(length=1, seqnum=0x0A)
	protected byte estimatedFinishTime;
	
	@FieldDesc(length=1, seqnum=0x0B)
	protected byte source;
	
	//following fields are not defined with length yet

	public ClientChargingStatusReq(long chargerId, long sessionId, byte chargerPortId, byte status, byte errorCode,
			int meterValue, byte batteryEnergy, short chargingVoltage, short chargingCurrency, byte estimatedFinishTime,
			byte source) {
		super(Instructions.CLIENT_CHARGING_FAULT, chargerId);
		this.sessionId = sessionId;
		this.chargerPortId = chargerPortId;
		this.status = status;
		this.errorCode = errorCode;
		this.meterValue = meterValue;
		this.batteryEnergy = batteryEnergy;
		this.chargingVoltage = chargingVoltage;
		this.chargingCurrency = chargingCurrency;
		this.estimatedFinishTime = estimatedFinishTime;
		this.source = source;
		
	}

	@Override
	public long getSessionId() {
		return sessionId;
	}

	@Override
	public byte getChargerPortId() {
		return chargerPortId;
	}

	public byte getStatus() {
		return status;
	}

	public byte getErrorCode() {
		return errorCode;
	}

	public int getMeterValue() {
		return meterValue;
	}

	public byte getBatteryEnergy() {
		return batteryEnergy;
	}

	public short getChargingVoltage() {
		return chargingVoltage;
	}

	public short getChargingCurrency() {
		return chargingCurrency;
	}

	public byte getEstimatedFinishTime() {
		return estimatedFinishTime;
	}

	public byte getSource() {
		return source;
	}

}

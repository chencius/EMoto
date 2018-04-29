package com.emoto.protocol.command;

import com.emoto.protocol.fields.ChargeSource;
import com.emoto.protocol.fields.ChargeStatus;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientChargingStatusReq implements CmdBase, IPortBasedCmd, ISessionBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargePortId;
	
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
	
	@FieldDesc(length=4, seqnum=0x0C)
	protected String offlineChargingId;

	@FieldDesc(length=2, seqnum=0x7F)
	protected String signature;
	
	public ClientChargingStatusReq() {
		
	}
	
	public ClientChargingStatusReq(long chargeId, long sessionId, byte chargePortId, ChargeStatus status, ErrorCode errorCode,
			int meterValue, byte batteryEnergy, short chargingVoltage, short chargingCurrency, byte estimatedFinishTime,
			ChargeSource source, String offlineChargingId, String signature) {
		this.instruction = Instructions.CLIENT_CHARGING_STATUS.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
		this.chargePortId = chargePortId;
		this.status = status.getValue();
		this.errorCode = errorCode.getValue();
		this.meterValue = meterValue;
		this.batteryEnergy = batteryEnergy;
		this.chargingVoltage = chargingVoltage;
		this.chargingCurrency = chargingCurrency;
		this.estimatedFinishTime = estimatedFinishTime;
		this.source = source.getValue();
		this.offlineChargingId = offlineChargingId;
		this.signature = signature;
	}

	@Override
	public Instructions getInstruction() {
		return Instructions.valueOf(instruction);
	}
	
	@Override
	public void setInstruction(byte instruction)
	{
		this.instruction = instruction;
	}
	
	@Override
	public long getChargeId() {
		return chargeId;
	}
	
	@Override
	public long getSessionId() {
		return sessionId;
	}

	@Override
	public byte getChargePortId() {
		return chargePortId;
	}

	public ChargeStatus getStatus() {
		return ChargeStatus.valueOf(status);
	}

	public ErrorCode getErrorCode() {
		return ErrorCode.valueOf(errorCode);
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

	public ChargeSource getSource() {
		return ChargeSource.valueOf(source);
	}

	@Override
	public String toString() {//need to update
		return String.format("command=%s, chargeId=%d, sessionId=%d, chargePortId=%d, status=%s, errorCode=%s, "
				+ "meterValue=%d, batteryEnergy=%d, chargingVoltage=%d, chargeingCurrency=%d, estimatedFinishTime=%d, source=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, chargePortId, getStatus(), getErrorCode(),
				meterValue, batteryEnergy, chargingVoltage, chargingCurrency, estimatedFinishTime, getSource());
	}
}

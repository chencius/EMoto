package com.emoto.protocol.command;

import com.emoto.protocol.fields.ChargeSource;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientConnectSucceedReq implements CmdBase, IPortBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargePortId;
	
	@FieldDesc(length=4, seqnum=0x04)
	protected int meterValue;
	
	@FieldDesc(length=1, seqnum=0x05)
	protected byte source;
	
	@FieldDesc(length=4, seqnum=0x06)
	protected String offlineChargingId;
	
	@FieldDesc(length=4, seqnum=0x07)
	protected String cardId;
	
	@FieldDesc(length=4, seqnum=0x08)
	protected String batteryId;
	
	@FieldDesc(length=2, seqnum=0x7F)
	protected String signature;

	public ClientConnectSucceedReq()
	{
	}
	
	public ClientConnectSucceedReq(long chargeId, long sessionId, byte chargePortId, int meterValue,
			ChargeSource source, String offlineChargingId, String cardId, String batteryId, String signature) {
		this.instruction = Instructions.CLIENT_CHARGING_STARTED.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
		this.chargePortId = chargePortId;
		this.meterValue = meterValue;
		this.source = source.getValue();
		this.offlineChargingId = offlineChargingId;
		this.cardId = cardId;
		this.batteryId = batteryId;
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
	
	public long getSessionId() {
		return sessionId;
	}

	@Override
	public byte getChargePortId() {
		return chargePortId;
	}

	public int getMeterValue() {
		return meterValue;
	}

	public byte getSource() {
		return source;
	}
	
	@Override
	public String toString() {//need to update here
		return String.format("command=%s, chargeId=%d, sessionId=%d, chargePortId=%d, meterValue=%d, source=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, chargePortId, meterValue, getSource());
	}
}

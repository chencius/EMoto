package com.emoto.protocol.command;

import com.emoto.protocol.fields.ChargeSource;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientDisconnectSucceedReq implements CmdBase, IPortBasedCmd, ISessionBasedCmd {
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

	@FieldDesc(length=1, seqnum=0x06)
	protected byte errorCode;
	
	@FieldDesc(length=4, seqnum=0x07)
	protected String offlineChargingId;
	
	@FieldDesc(length=4, seqnum=0x08)
	protected String cardId;
	
	@FieldDesc(length=4, seqnum=0x09)
	protected int expense;
	
	@FieldDesc(length=4, seqnum=0x7F)
	protected String signature;
	
	public ClientDisconnectSucceedReq() {
	}
	
	public ClientDisconnectSucceedReq(long chargeId, long sessionId, byte chargePortId, int meterValue,
			ChargeSource source, ErrorCode errorCode, String offlineChargingId, String cardId, int expense, String signature) {
		this.instruction = Instructions.CLIENT_CHARGING_STOPPED.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
		this.chargePortId = chargePortId;
		this.meterValue = meterValue;
		this.source = source.getValue();
		this.errorCode = errorCode.getValue();
		this.offlineChargingId = offlineChargingId;
		this.cardId = cardId;
		this.expense = expense;
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

	public ChargeSource getSource() {
		return ChargeSource.valueOf(source);
	}

	public ErrorCode getErrorCode() {
		return ErrorCode.valueOf(errorCode);
	}
	
	@Override
	public String toString() {//need to update
		return String.format("command=%s, chargeId=%d, sessionId=%d, chargePortId=%d, meterValue=%d, source=%s, errorCode=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, chargePortId, meterValue, getSource(), getErrorCode());
	}
}

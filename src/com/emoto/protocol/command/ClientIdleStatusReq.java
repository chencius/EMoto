package com.emoto.protocol.command;

import com.emoto.protocol.fields.ChargeStatus;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientIdleStatusReq implements CmdBase, IPortBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=1, seqnum=0x02)
	protected byte chargePortId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte status;
	
	@FieldDesc(length=1, seqnum=0x04)
	protected byte errorCode;
	
	@FieldDesc(length=4, seqnum=0x05)
	protected int meterValue;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ClientIdleStatusReq() {
		
	}
	
	public ClientIdleStatusReq(long chargeId, byte chargePortId, ChargeStatus status, ErrorCode errorCode,
			int meterValue, String signature) {
		this.instruction = Instructions.CLIENT_IDLE_STATUS.getValue();
		this.chargeId = chargeId;
		this.chargePortId = chargePortId;
		this.status = status.getValue();
		this.errorCode = errorCode.getValue();
		this.meterValue = meterValue;
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

	public String getSignature() {
		return signature;
	}

	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, chargePortId=%d, status=%s, errorCode=%s, "
				+ "meterValue=%d, signature=%s",
				this.getClass().getSimpleName(), chargeId, chargePortId, getStatus(), getErrorCode(),
				meterValue, signature);
	}
}

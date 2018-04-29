package com.emoto.protocol.command;

import com.emoto.protocol.fields.ChargeSource;
import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientChargingFaultReq implements CmdBase, IPortBasedCmd, ISessionBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
    @FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargePortId;
	
	@FieldDesc(length=1, seqnum=0x04)
	protected byte errorCode;
	
	@FieldDesc(length=1, seqnum=0x05)
	protected byte source;
	
	//following fields are not defined with length yet

	public ClientChargingFaultReq()
	{
	}
	
	public ClientChargingFaultReq(long chargeId, long sessionId, byte chargePortId, ErrorCode errorCode, ChargeSource source) {
		this.instruction = Instructions.CLIENT_CHARGING_FAULT.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
		this.chargePortId = chargePortId;
		this.errorCode = errorCode.getValue();
		this.source = source.getValue();
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

	public ErrorCode getErrorCode() {
		return ErrorCode.valueOf(errorCode);
	}

	public ChargeSource getSource() {
		return ChargeSource.valueOf(source);
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, sessionId=%d, chargePortId=%d, errorCode=%s, source=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, chargePortId, getErrorCode(), getSource());
	}
}

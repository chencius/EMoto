package com.emoto.protocol.command;

import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerStartChargingResp implements CmdBase, IPortBasedCmd, ISessionBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargePortId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;

	public ServerStartChargingResp() {
		
	}
	
	public ServerStartChargingResp(long chargeId, long sessionId, byte chargePortId, ErrorCode errorCode) {
		this.instruction = Instructions.SERVER_START_CHARGING.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
		this.chargePortId = chargePortId;
		this.errorCode = errorCode.getValue();
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

	public byte getChargePortId() {
		return chargePortId;
	}

	public ErrorCode getErrorCode() {
		return ErrorCode.valueOf(errorCode);
	}	
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, sessionId=%d, chargerPortId=%d, errorCode=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, chargePortId, getErrorCode());
	}
}

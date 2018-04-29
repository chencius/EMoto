package com.emoto.protocol.command;

import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientEmergentButtonReleaseResp implements CmdBase {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;

	public ClientEmergentButtonReleaseResp() {
	}
	
	public ClientEmergentButtonReleaseResp(long chargeId, ErrorCode errorCode) {
		this.instruction = Instructions.CLIENT_EMERGENT_BUTTON_RELEASEED.getValue();
		this.chargeId = chargeId;
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
	
	public ErrorCode getErrorCode() {
		return ErrorCode.valueOf(errorCode);
	}

	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, errorCode=%s",
				this.getClass().getSimpleName(), chargeId, getErrorCode());
	}
}

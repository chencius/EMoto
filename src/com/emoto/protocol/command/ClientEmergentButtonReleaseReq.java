package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientEmergentButtonReleaseReq implements CmdBase {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ClientEmergentButtonReleaseReq() {
	}
	
	public ClientEmergentButtonReleaseReq(long chargerId, String signature) {
		this.instruction = Instructions.CLIENT_EMERGENT_BUTTON_RELEASEED.getValue();
		this.chargeId = chargeId;
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
	
	public String getSignature() {
		return signature;
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, signature=%s",
				this.getClass().getSimpleName(), chargeId, signature);
	}
}

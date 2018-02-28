package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public abstract class CmdBase {
    protected byte instruction;
    
    @FieldDesc(length=8, seqnum=0x01)
	protected long chargerId;
	
	public byte getInstruction() {
		return instruction;
	}
	
	public long getChargerId() {
		return chargerId;
	}
	
	public CmdBase(Instructions instruction, long chargeId) {
		this.instruction = (byte)(instruction.getValue());
		this.chargerId = chargeId;
	}
}

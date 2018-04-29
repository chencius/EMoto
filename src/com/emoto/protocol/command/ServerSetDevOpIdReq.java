package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerSetDevOpIdReq implements CmdBase, ISessionBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected String hwUniqueId;
	
	@FieldDesc(length=8, seqnum=0x03)
	protected long sessionId;
	
	@FieldDesc(length=8, seqnum=0x04)
	protected long destChargeId;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ServerSetDevOpIdReq() {
	}
	
	public ServerSetDevOpIdReq(long chargeId, String hwUniqueId, long sessionId, long destChargeId, String signature) {
		this.instruction = Instructions.SET_DEV_OP_ID.getValue();
		this.chargeId = chargeId;
		this.hwUniqueId = hwUniqueId;
		this.sessionId = sessionId;
		this.destChargeId = destChargeId;
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
	
	public String getHwUniqueId() {
		return hwUniqueId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public long getDestChargeId() {
		return destChargeId;
	}

	public String getSignature() {
		return signature;
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, hwUniqueId=%s, sessionId=%d, destChargerId=%d, signature=%s",
				this.getClass().getSimpleName(), chargeId, hwUniqueId, sessionId, destChargeId, signature);
	}
}

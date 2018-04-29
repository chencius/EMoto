package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerStartChargingReq implements CmdBase, IPortBasedCmd, ISessionBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargePortId;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ServerStartChargingReq() {
		
	}
	
	public ServerStartChargingReq(long chargeId, long sessionId, byte chargePortId, String signature) {
		this.instruction = Instructions.SERVER_START_CHARGING.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
		this.chargePortId = chargePortId;
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

	public byte getChargePortId() {
		return chargePortId;
	}

	public String getSignature() {
		return signature;
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, sessionId=%d, chargePortId=%d, signature=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, chargePortId, signature);
	}
}

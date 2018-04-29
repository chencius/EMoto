package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;
import com.emoto.protocol.fields.OpType;

public class ServerStartStopChargerReq implements CmdBase, ISessionBasedCmd {	
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte opType;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ServerStartStopChargerReq() {
		
	}
	
	public ServerStartStopChargerReq(long chargeId, long sessionId, OpType opType, String signature) {
		this.instruction = Instructions.SERVER_START_STOP_CHARGER.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
		this.opType = opType.getValue();
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

	public byte getOpType() {
		return opType;
	}

	public String getSignature() {
		return signature;
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, sessionId=%d, opType=%s, signature=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, getOpType(), signature);
	}
}

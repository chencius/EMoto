package com.emoto.protocol.command;

import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerSetDevOpIdResp implements CmdBase, ISessionBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected String hwUniqueId;
	
	@FieldDesc(length=8, seqnum=0x03)
	protected long sessionId;
	
	@FieldDesc(length=8, seqnum=0x04)
	protected long destChargeId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;

	public ServerSetDevOpIdResp() {
		
	}
	
	public ServerSetDevOpIdResp(long chargeId, String hwUniqueId, long sessionId, long destChargeId, ErrorCode errorCode) {
		this.instruction = Instructions.SET_DEV_OP_ID.getValue();
		this.chargeId = chargeId;
		this.hwUniqueId = hwUniqueId;
		this.sessionId = sessionId;
		this.destChargeId = destChargeId;
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
	
	public String getHwUniqueId() {
		return hwUniqueId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public long getDestChargeId() {
		return destChargeId;
	}

	public ErrorCode getErrorCode() {
		return ErrorCode.valueOf(errorCode);
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, hwUniqueId=%s, sessionId=%d, destChargeId=%d, errorCode=%s",
				this.getClass().getSimpleName(), chargeId, hwUniqueId, sessionId, destChargeId, getErrorCode());
	}
}

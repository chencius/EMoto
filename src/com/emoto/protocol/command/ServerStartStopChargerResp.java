package com.emoto.protocol.command;

import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerStartStopChargerResp implements CmdBase, ISessionBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;
	
	public ServerStartStopChargerResp() {
		
	}
	
	public ServerStartStopChargerResp(long chargeId, long sessionId, ErrorCode errorCode) {
		this.instruction = Instructions.SERVER_START_STOP_CHARGER.getValue();
		this.chargeId = chargeId;
		this.sessionId = sessionId;
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

	public ErrorCode getErrorCode() {
		return ErrorCode.valueOf(errorCode);
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, sessionId=%d, errorCode=%s",
				this.getClass().getSimpleName(), chargeId, sessionId, getErrorCode());
	}
}

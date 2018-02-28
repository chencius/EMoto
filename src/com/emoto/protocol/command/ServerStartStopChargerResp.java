package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerStartStopChargerResp extends CmdBase {
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;
	
	public ServerStartStopChargerResp(long chargerId, long sessionId, byte errorCode) {
		super(Instructions.SERVER_START_STOP_CHARGER_REQ, chargerId);
		this.sessionId = sessionId;
		this.errorCode = errorCode;
	}

	public long getSessionId() {
		return sessionId;
	}

	public byte getErrorCode() {
		return errorCode;
	}
}

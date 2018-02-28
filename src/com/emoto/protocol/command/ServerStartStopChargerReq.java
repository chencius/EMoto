package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerStartStopChargerReq extends CmdBase {	
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte opType;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ServerStartStopChargerReq(long chargerId, long sessionId, byte opType, String signature) {
		super(Instructions.SERVER_START_STOP_CHARGER_REQ, chargerId);
		this.sessionId = sessionId;
		this.opType = opType;
		this.signature = signature;
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
}

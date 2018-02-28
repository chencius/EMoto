package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerStartChargingResp extends CmdBase {
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected long chargerPortId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;

	public ServerStartChargingResp(long chargerId, long sessionId, long chargerPortId, byte errorCode) {
		super(Instructions.SERVER_START_CHARGING_REQ, chargerId);
		this.sessionId = sessionId;
		this.chargerPortId = chargerPortId;
		this.errorCode = errorCode;
	}

	public long getSessionId() {
		return sessionId;
	}

	public long getChargerPortId() {
		return chargerPortId;
	}

	public byte getErrorCode() {
		return errorCode;
	}	
}

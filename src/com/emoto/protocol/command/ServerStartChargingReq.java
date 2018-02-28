package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerStartChargingReq extends CmdBase {
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected long chargerPortId;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ServerStartChargingReq(long chargerId, long sessionId, long chargerPortId, String signature) {
		super(Instructions.SERVER_START_CHARGING_REQ, chargerId);
		this.sessionId = sessionId;
		this.chargerPortId = chargerPortId;
		this.signature = signature;
	}

	public long getSessionId() {
		return sessionId;
	}

	public long getChargerPortId() {
		return chargerPortId;
	}

	public String getSignature() {
		return signature;
	}
	
	
}

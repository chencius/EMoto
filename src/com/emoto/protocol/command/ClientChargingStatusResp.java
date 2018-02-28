package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientChargingStatusResp extends CmdBase implements IPortBasedCmd {
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargerPortId;
	
	@FieldDesc(length=1, seqnum=0x6f)
	protected byte errorCode;

	public ClientChargingStatusResp(long chargerId, long sessionId, byte chargerPortId, byte errorCode) {
		super(Instructions.CLIENT_CHARGING_STATUS, chargerId);
		this.sessionId = sessionId;
		this.chargerPortId = chargerPortId;
		this.errorCode = errorCode;
	}

	public int getSessionId() {
		return sessionId;
	}

	@Override
	public byte getChargerPortId() {
		return chargerPortId;
	}

	public byte getErrorCode() {
		return errorCode;
	}
}

package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ServerSetDevOpIdResp extends CmdBase {
	@FieldDesc(length=8, seqnum=0x02)
	protected String hwUniqueId;
	
	@FieldDesc(length=8, seqnum=0x03)
	protected long sessionId;
	
	@FieldDesc(length=8, seqnum=0x04)
	protected long destChargerId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;

	public ServerSetDevOpIdResp(long chargerId, String hwUniqueId, long sessionId, long destChargerId, byte errorCode) {
		super(Instructions.SET_DEV_OP_ID, chargerId);
		this.hwUniqueId = hwUniqueId;
		this.sessionId = sessionId;
		this.destChargerId = destChargerId;
		this.errorCode = errorCode;
	}

	public String getHwUniqueId() {
		return hwUniqueId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public long getDestChargerId() {
		return destChargerId;
	}

	public byte getErrorCode() {
		return errorCode;
	}
}

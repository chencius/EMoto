package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientEmergentButtonReleaseResp extends CmdBase {
	@FieldDesc(length=1, seqnum=0x7F)
	protected byte errorCode;

	public ClientEmergentButtonReleaseResp(long chargerId, byte errorCode) {
		super(Instructions.CLIENT_EMERGENT_BUTTON_RELEASE, chargerId);
		this.errorCode = errorCode;
	}

	public byte getErrorCode() {
		return errorCode;
	}

}

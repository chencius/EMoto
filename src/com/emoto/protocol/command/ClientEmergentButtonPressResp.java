package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientEmergentButtonPressResp extends CmdBase {
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;

	public ClientEmergentButtonPressResp(long chargerId, byte errorCode) {
		super(Instructions.CLIENT_EMERGENT_BUTTON_PRESS, chargerId);
		this.errorCode = errorCode;
	}

	public byte getErrorCode() {
		return errorCode;
	}
}

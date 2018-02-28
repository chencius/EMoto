package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientEmergentButtonReleaseReq extends CmdBase {
	@FieldDesc(length=8, seqnum=0x7F)
	protected long signature;

	public ClientEmergentButtonReleaseReq(long chargerId, long signature) {
		super(Instructions.CLIENT_EMERGENT_BUTTON_RELEASE, chargerId);
		this.signature = signature;
	}

	public long getSignature() {
		return signature;
	}
}

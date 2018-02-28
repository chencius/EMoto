package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientBikeConnectedReq extends CmdBase implements IPortBasedCmd {
	@FieldDesc(length=1, seqnum=0x02)
	protected byte chargerPortId;
	
	@FieldDesc(length=4, seqnum=0x03)
	protected int localId;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected long signature;

	public ClientBikeConnectedReq(long chargerId, byte chargerPortId, int localId, long signature) {
		super(Instructions.CLIENT_EMERGENT_BUTTON_PRESS, chargerId);
		this.chargerPortId = chargerPortId;
		this.localId = localId;
		this.signature = signature;
	}

	@Override
	public byte getChargerPortId() {
		return chargerPortId;
	}

	public int getLocalId() {
		return localId;
	}

	public long getSignature() {
		return signature;
	}
}

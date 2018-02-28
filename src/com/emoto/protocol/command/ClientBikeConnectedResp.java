package com.emoto.protocol.command;

import com.emoto.protocol.fields.ErrorCode;
import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientBikeConnectedResp extends CmdBase {
	@FieldDesc(length=1, seqnum=0x02)
	protected byte chargerPortId;
	
	@FieldDesc(length=4, seqnum=0x03)
	protected int localId;
	
	@FieldDesc(length=1, seqnum=0x6F)
	protected byte errorCode;

	public ClientBikeConnectedResp(long chargerId, byte chargerPortId, int localId, ErrorCode errorCode) {
		super(Instructions.SERVER_START_CHARGING_REQ, chargerId);
		this.chargerPortId = chargerPortId;
		this.localId = localId;
		this.errorCode = errorCode.getValue();
	}
}

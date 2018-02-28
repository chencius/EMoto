package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientConnectSucceedReq extends CmdBase implements IPortBasedCmd {
	@FieldDesc(length=8, seqnum=0x02)
	protected byte sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargerPortId;
	
	@FieldDesc(length=4, seqnum=0x04)
	protected int meterValue;
	
	@FieldDesc(length=1, seqnum=0x05)
	protected byte source;

	public ClientConnectSucceedReq(long chargerId, byte chargerPortId, int meterValue, byte source) {
		super(Instructions.CLIENT_CONNECTION_SUCC, chargerId);
		this.chargerPortId = chargerPortId;
		this.meterValue = meterValue;
		this.source = source;
	}

	public int getSessionId() {
		return sessionId;
	}

	@Override
	public byte getChargerPortId() {
		return chargerPortId;
	}

	public int getMeterValue() {
		return meterValue;
	}

	public byte getSource() {
		return source;
	}
}

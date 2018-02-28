package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientChargingFault extends CmdBase implements IPortBasedCmd, ISessionBasedCmd {
	@FieldDesc(length=8, seqnum=0x02)
	protected long sessionId;
	
	@FieldDesc(length=1, seqnum=0x03)
	protected byte chargerPortId;
	
	@FieldDesc(length=1, seqnum=0x04)
	protected byte errorCode;
	
	@FieldDesc(length=1, seqnum=0x05)
	protected byte source;
	
	//following fields are not defined with length yet

	public ClientChargingFault(long chargerId, long sessionId, byte chargerPortId, byte errorCode, byte source) {
		super(Instructions.CLIENT_CHARGING_FAULT, chargerId);
		this.sessionId = sessionId;
		this.chargerPortId = chargerPortId;
		this.errorCode = errorCode;
		this.source = source;
	}

	@Override
	public long getSessionId() {
		return sessionId;
	}

	@Override
	public byte getChargerPortId() {
		return chargerPortId;
	}

	public byte getErrorCode() {
		return errorCode;
	}

	public long getSource() {
		return source;
	}
}

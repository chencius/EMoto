package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientBikeConnectedReq implements CmdBase, IPortBasedCmd {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=1, seqnum=0x02)
	protected byte chargePortId;
	
	@FieldDesc(length=4, seqnum=0x03)
	protected int localId;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ClientBikeConnectedReq()
	{

	}
	
	public ClientBikeConnectedReq(long chargeId, byte chargePortId, int localId, String signature) {
		this.instruction = Instructions.CLIENT_BIKE_CONNECTED.getValue();
		this.chargeId = chargeId;
		this.chargePortId = chargePortId;
		this.localId = localId;
		this.signature = signature;
	}
	
	@Override
	public Instructions getInstruction() {
		return Instructions.valueOf(instruction);
	}
	
	@Override
	public void setInstruction(byte instruction)
	{
		this.instruction = instruction;
	}
	
	@Override
	public long getChargeId() {
		return chargeId;
	}
	
	@Override
	public byte getChargePortId() {
		return chargePortId;
	}

	public int getLocalId() {
		return localId;
	}

	public String getSignature() {
		return signature;
	}
	
	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, chargePortId=%d, localId=%d, signature=%s",
				this.getClass().getSimpleName(), chargeId, chargePortId, localId, signature);
	}
}

package com.emoto.protocol.command;

import com.emoto.protocol.fields.FieldDesc;
import com.emoto.protocol.fields.Instructions;

public class ClientLoginReq implements CmdBase {
	protected byte instruction;
    
	@FieldDesc(length=8, seqnum=0x01)
	protected long chargeId;
	
	@FieldDesc(length=8, seqnum=0x02)
	protected String hwId;
	
	@FieldDesc(length=8, seqnum=0x03)
	protected String swVer;
	
	@FieldDesc(length=1, seqnum=0x04)
	protected byte protocolVer;
	
	@FieldDesc(length=4, seqnum=0x05)
	protected int localId;
	
	@FieldDesc(length=8, seqnum=0x06)
	protected String swName;
	
	@FieldDesc(length=8, seqnum=0x07)
	protected long compileTime;
	
	@FieldDesc(length=8, seqnum=0x7F)
	protected String signature;

	public ClientLoginReq()
	{

	}
	
	public ClientLoginReq(long chargeId, String hwId, String swVer,
			byte protocolVer, int localId, String swName, long compileTime, String signature) {
		this.instruction = Instructions.CLIENT_LOGIN.getValue();
		this.chargeId = chargeId;
		this.hwId = hwId;
		this.swVer = swVer;
		this.protocolVer = protocolVer;
		this.localId = localId;
		this.swName = swName;
		this.compileTime = compileTime;
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
	
	public long getChargeId() {
		return chargeId;
	}

	public String getHwId() {
		return hwId;
	}

	public String getSwVer() {
		return swVer;
	}

	public byte getProtocolVer() {
		return protocolVer;
	}

	public int getLocalId() {
		return localId;
	}

	public String getSwName() {
		return swName;
	}

	public long getCompileTime() {
		return compileTime;
	}

	public String getSignature() {
		return signature;
	}

	@Override
	public String toString() {
		return String.format("command=%s, chargeId=%d, hwId=%s, swVer=%s, protocolVer=%d, localId=%d, "
				+ "swName=%s, compileTime=%d, signature=%s",
				this.getClass().getSimpleName(), chargeId, hwId, swVer, protocolVer, localId, swName,
				compileTime, signature);
	}
}

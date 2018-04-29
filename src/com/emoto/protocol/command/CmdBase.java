package com.emoto.protocol.command;

import com.emoto.protocol.fields.Instructions;

public interface CmdBase {
    
	public Instructions getInstruction();
	
	public void setInstruction(byte instruction);
	
	public long getChargeId();
	
}

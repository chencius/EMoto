package com.emoto.protocol.fields;

public class ValueReturnedStatus implements ValueReturned {
	private boolean status;
	
	@Override
	public boolean getStatus() {
		return this.status;
	}
	
	@Override
	public void setStatus(boolean status) {
		this.status = status;
	}
}

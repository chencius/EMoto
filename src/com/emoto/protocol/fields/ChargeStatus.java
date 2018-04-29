package com.emoto.protocol.fields;

public enum ChargeStatus {
	AVAILABLE(1),
	CONNECTED(2),
	CHARGING(4),
	FAULTED(64),
	INVALID(255);
	
	private final int m_value;
	
	private ChargeStatus(int value) {
		m_value = value;
	}
	
	public byte getValue() {
		return (byte)m_value;
	}
	
	public static ChargeStatus valueOf(int value) {
		switch(value) {
		case 1:
			return AVAILABLE;
		case 2:
			return CONNECTED;
		case 4:
			return CHARGING;
		case 64:
			return FAULTED;
		}
		return INVALID;
	}
}

package com.emoto.protocol.fields;

public enum OpType {
	STARTUP(1),
	RESTART(3),
	SHUTDOWN(5),
	INVALID(255);
	
	private final int m_value;
	
	private OpType(int value) {
		m_value = value;
	}
	
	public byte getValue() {
		return (byte)m_value;
	}
	
	public static OpType valueOf(int value) {
		switch(value) {
		case 1:
			return STARTUP;
		case 3:
			return RESTART;
		case 5:
			return SHUTDOWN;
		}
		return INVALID;
	}
}

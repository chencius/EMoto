package com.emoto.protocol.fields;

public enum ChargeSource {
	UNKNOWN(0),
	SERVER_CHARGING(1),
	CARD_CHARGING(2),
	PASSWD_CHARGING(3),
	PLUG_AND_CHARGING(4),
	INVALID(255);
	
	private int m_value;
	
	private ChargeSource(int value) {
		this.m_value = value;
	}
	
	public byte getValue() {
		return (byte)m_value;
	}
	
	public static ChargeSource valueOf(int value) {
		switch(value) {
		case 0:
			return UNKNOWN;
		case 1:
			return SERVER_CHARGING;
		case 2:
			return CARD_CHARGING;
		case 3:
			return PASSWD_CHARGING;
		case 4:
			return PLUG_AND_CHARGING;
		default:
			return INVALID;
		}
	}
}

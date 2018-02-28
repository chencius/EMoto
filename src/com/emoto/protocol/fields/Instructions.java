package com.emoto.protocol.fields;

public enum Instructions {
	SERVER_START_STOP_CHARGER_REQ(0x01),
	SERVER_START_CHARGING_REQ(0x05),
	SERVER_STOP_CHARGING_REQ(0x06),
	
	UPGRADE_SW_VERSION(0x20),
	SET_DEV_OP_ID(0x21),
	
	CLIENT_EMERGENT_BUTTON_PRESS(0x6A),
	CLIENT_EMERGENT_BUTTON_RELEASE(0x6B),
	
	CLIENT_LOG_REPORT(0x70),
	CLIENT_VERIFY_BAT_SEQ(0x71),
	CLIENT_BIKE_CONNECTTED(0x75),
	CLIENT_BIKE_DISCONNECTTED(0x76),
	CLIENT_DL_SW(0x7F),
	CLIENT_SW_UPG_RESULT(0x80),
	CLIENT_START(0x81),
	CLIENT_LOGIN(0x82),
	CLIENT_HEARTBEAT(0x83),
	CLIENT_FAULT(0x84),
	CLIENT_CONNECTION_SUCC(0x85),
	CLIENT_DISCONNECTION_SUCC(0x86),
	CLIENT_STATE_CHANGE(0x89),
	CLIENT_CHARGING_STATUS(0x93),
	CLIENT_CHARGING_FAULT(0x94),
	INVALID(0xFF);
	
	private final int m_value;
	private Instructions(int value)
	{
		m_value = value;
	}
	
	public byte getValue()
	{
		return (byte)m_value;
	}
	
	public static Instructions valueOf(int value) {
		switch(value) {
		case 0x01:
			return SERVER_START_STOP_CHARGER_REQ;
		case 0x05:
			return SERVER_START_CHARGING_REQ;
		case 0x06:
			return SERVER_STOP_CHARGING_REQ;
		case 0x20:
			return UPGRADE_SW_VERSION;
		case 0x21:
			return SET_DEV_OP_ID;
		case 0x6A:
			return CLIENT_EMERGENT_BUTTON_PRESS;
		case 0x6B:
			return CLIENT_EMERGENT_BUTTON_RELEASE;
		default:
			return INVALID;
		}
	}
}

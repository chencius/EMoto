package com.emoto.protocol.fields;

public enum Instructions {
	SERVER_START_STOP_CHARGER(0x01),
	SERVER_START_CHARGING(0x05),
	SERVER_STOP_CHARGING(0x06),
	
	UPGRADE_SW_VERSION(0x20),
	SET_DEV_OP_ID(0x21),
	
	CLIENT_EMERGENT_BUTTON_PRESSED(0x6A),
	CLIENT_EMERGENT_BUTTON_RELEASEED(0x6B),
	
	CLIENT_LOG_REPORT(0x70),
	CLIENT_VERIFY_BAT_UNIQUE_SN(0x71),
	CLIENT_BIKE_CONNECTED(0x75),
	CLIENT_BIKE_DISCONNECTED(0x76),
	CLIENT_DL_SW(0x7F),
	CLIENT_SW_UPG_RESULT(0x80),
	CLIENT_STARTED(0x81),
	CLIENT_LOGIN(0x82),
	CLIENT_IDLE_STATUS(0x83),
	CLIENT_IDLE_FAULT(0x84),
	CLIENT_CHARGING_STARTED(0x85),
	CLIENT_CHARGING_STOPPED(0x86),
	CLIENT_STATE_CHANGED(0x89),
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
		int temp = value & 0xFF;
		switch(temp) {
		case 0x01:
			return SERVER_START_STOP_CHARGER;
		case 0x05:
			return SERVER_START_CHARGING;
		case 0x06:
			return SERVER_STOP_CHARGING;
		case 0x20:
			return UPGRADE_SW_VERSION;
		case 0x21:
			return SET_DEV_OP_ID;
		case 0x6A:
			return CLIENT_EMERGENT_BUTTON_PRESSED;
		case 0x6B:
			return CLIENT_EMERGENT_BUTTON_RELEASEED;
		case 0x70:
			return CLIENT_LOG_REPORT;
		case 0x71:
			return CLIENT_VERIFY_BAT_UNIQUE_SN;
		case 0x75:
			return CLIENT_BIKE_CONNECTED;
		case 0x76:
			return CLIENT_BIKE_DISCONNECTED;
		case 0x7f:
			return CLIENT_DL_SW;
		case (int)0x80:
			return CLIENT_SW_UPG_RESULT;
		case (int)0x81:
			return CLIENT_STARTED;
		case (int)0x82:
			return CLIENT_LOGIN;
		case 0x83:
			return CLIENT_IDLE_STATUS;
		case 0x84:
			return CLIENT_IDLE_FAULT;
		case 0x85:
			return CLIENT_CHARGING_STARTED;
		case 0x86:
			return CLIENT_CHARGING_STOPPED;
		case 0x89:
			return CLIENT_STATE_CHANGED;
		case 0x93:
			return CLIENT_CHARGING_STATUS;
		case 0x94:
			return CLIENT_CHARGING_FAULT;
		default:
			return INVALID;
		}
	}
}

package com.emoto.protocol.fields;

public enum ErrorCode {
	ACT_SUCCEDED(0),
	CHARGE_ID_ERR(1),
	CHARGE_SESSION_ERR(2),
	CHARGE_PORT_ERR(3),
	CHARGE_SIGN_ERR(4),
	CHARGER_NOT_IDLE(5),
	CHARGER_RUNNING(6),
	CHARGER_NOT_RUNNING(7),
	DUP_CMD(8),
	INVALID(9);
	
	private final int m_value;
	private ErrorCode(int value) {
		m_value = value;
	}
	
	public byte getValue()
	{
		return (byte)m_value;
	}
	
	public static ErrorCode valueOf(int value) {
		switch(value) {
		case 0:
			return ACT_SUCCEDED;
		case 1:
			return CHARGE_ID_ERR;
		case 2:
			return CHARGE_SESSION_ERR;
		case 3:
			return CHARGE_PORT_ERR;
		case 4:
			return CHARGE_SIGN_ERR;
		case 5:
			return CHARGER_NOT_IDLE;
		case 6:
			return CHARGER_RUNNING;
		case 7:
			return CHARGER_NOT_RUNNING;
		case 8:
			return DUP_CMD;
		default:
			return INVALID;
		}
	}

}

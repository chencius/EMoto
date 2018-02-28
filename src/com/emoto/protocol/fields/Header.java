package com.emoto.protocol.fields;

public class Header {
	public static final byte[] SERVER_STX = {0x53, 0x53}; 
	public static final byte[] CLIENT_STX = {0x43, 0x53};
	public static final byte ETX = 0x45;
}

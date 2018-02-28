package com.emoto.network.tcp;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.emoto.server.Server;
import com.emoto.statemachine.ChargePoint;
import com.emoto.statemachine.State;

public class TcpServer {
	private static AsyncServerHandler serverHandler;
	
	private static Logger logger = Logger.getLogger(TcpServer.class.getName());
	
	private TcpServer() {
	}
	
	public static void start(String ip, int port, Server server) throws IOException {
		if (serverHandler != null) {
			return;
		}
		
		synchronized(TcpServer.class) {
			serverHandler = new AsyncServerHandler(ip, port, server);
			serverHandler.start();
		}
	}
}

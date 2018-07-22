package com.emoto.network.tcp;

import java.io.IOException;
import java.util.logging.Logger;

import com.emoto.server.Server;

public class TcpServer {
	private static AsyncServerHandler serverHandler;
	private static Object callback;
	private static Logger logger = Logger.getLogger(TcpServer.class.getName());
	
	private TcpServer() {
	}
	
	public static void setCallback(Object callback) {
		TcpServer.callback = callback;
	}
	
	public static void start(String ip, int port, Server server) throws IOException {
		if (serverHandler != null) {
			return;
		}
		
		synchronized(TcpServer.class) {
			if (serverHandler == null) {
				serverHandler = new AsyncServerHandler(ip, port, server);
				serverHandler.setCallback(callback);
				serverHandler.start();
			}
		}
	}
}

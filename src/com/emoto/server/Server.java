package com.emoto.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.statemachine.ChargePoint;
import com.emoto.statemachine.State;

import com.emoto.network.tcp.TcpServer;

public class Server {
	private static final String systemVariable = "CONFIG_FILE";
	private static Logger logger = Logger.getLogger(Server.class.getName());
	private Properties prop;
	public Map<Long, ChargePoint> chargePoints = new HashMap<>();
	public long sessionId = 0;
	
	public static void main(String argv[]) {
		Server server = new Server();
		server.init();
	}
	
	private void init() {
		prop = new Properties();
		if (!loadConfig()) {
			return;
		};
		
		String ip = prop.getProperty(Prop.SERVER_IP.toString());
		int port = Integer.valueOf(prop.getProperty(Prop.SERVER_PORT.toString()));
		try {
			TcpServer.start(ip, port, this);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to start TcpServer ip={0} port={1}", new Object[]{ip, port});
			return;
		}
	}
	
	private boolean loadConfig()  {
		String cfgFile = System.getProperty(systemVariable);
		if (cfgFile == null) {
			logger.log(Level.WARNING, "System config file " + systemVariable + " is not defined");
			return false;
		}
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(cfgFile);
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, "Config file " + cfgFile + " is not found");
			return false;
		}
		try {
			prop.load(fis);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Load properties from config file " + cfgFile + " failed");
			return false;
		}
		return true;
	}
	
	private enum Prop {
		SERVER_IP(0x01) {
			public String toString() {
				return "server_ip";
			}
		},
		SERVER_PORT(0x02) {
			public String toString() {
				return "server_port";
			}
		};
		
		private final int m_value;
		private Prop(int value) {
			m_value = value;
		}
		
		public int getValue() {
			return m_value;
		}
		
		public static Prop valueOf(int value) {
			switch(value) {
			case 0x01:
				return SERVER_IP;
			case 0x02:
				return SERVER_PORT;
			default:
				logger.log(Level.WARNING, "Can't get property " + value + " in configuration file");
				return null;
			}
		}
	}
}

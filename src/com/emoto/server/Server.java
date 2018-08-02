package com.emoto.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.network.tcp.TcpServer;
import com.emoto.persistent.DBManagerFactory;
import com.emoto.persistent.DBSessionFactory;
import com.emoto.persistent.data.ChargeRecord;
import com.emoto.persistent.data.HWId2Barcode;
import com.emoto.statemachine.ChargePoint;

public class Server implements Runnable {
	private static final String systemVariable = "CONFIG_FILE";
	private static Logger logger = Logger.getLogger(Server.class.getName());
	private Properties prop;
	public Map<Long, ChargePoint> chargePoints = new ConcurrentHashMap<>();
	public Map<String, HWMapping> hwId2ChargePoint = new ConcurrentHashMap<>();
	public volatile AtomicLong sessionId = new AtomicLong(0);
	public volatile AtomicLong chargePointId = new AtomicLong(0);
	public DBManagerFactory<ChargeRecord, String> recordFactory;
	
	public Object callback;
	
	public static void main(String argv[]) {
		Server server = new Server();
		server.run();
	}
	
	public void run() {
		prop = new Properties();
		if (!loadConfig()) {
			return;
		};
		
		loadHWInfo();
		TcpServer.setCallback(callback);
		
		String ip = prop.getProperty(Prop.SERVER_IP.toString());
		int port = Integer.valueOf(prop.getProperty(Prop.SERVER_PORT.toString()));
		try {
			TcpServer.start(ip, port, this);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to start TcpServer ip={0} port={1}", new Object[]{ip, port});
			return;
		}
	}
	
	private void loadHWInfo() {
		DBManagerFactory<HWId2Barcode, String> hwDBFactory = new DBManagerFactory<>(DBSessionFactory.getSessionFactory());
		
		List<HWId2Barcode> HWList = hwDBFactory.listElement(); 
		for (HWId2Barcode hw : HWList) {
			HWMapping entry = new HWMapping();
			entry.barcodeId = hw.getBarcode();
			entry.chargeId = chargePointId.incrementAndGet();
			hwId2ChargePoint.put(hw.getHwId(), entry);
			
			System.out.println("load from DB: (" + hw.getHwId()+", " + hw.getBarcode() +", " + entry.chargeId + ")");
		}
		
		recordFactory = new DBManagerFactory<ChargeRecord, String>(DBSessionFactory.getSessionFactory());
	}
	
	private boolean loadConfig()  {
		//String cfgFile = System.getenv(systemVariable);
		String cfgFile = "/Users/chencius/config/config.xml";
		//String cfgFile = "/home/chargeserver/config/EMoto.cfg";

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
	
	public void setCallback(Object callback) {
		this.callback = callback;
	}
	
	public class HWMapping {
		public String barcodeId;
		public long chargeId;
	}
}

package com.emoto.persistent;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DBSessionFactory {
	public static SessionFactory factory = null;
	
	public static SessionFactory getSessionFactory() {
		if (factory != null) {
			return factory;
		} else {
			synchronized(DBSessionFactory.class) {
				if (factory == null) {
					Configuration config = new Configuration();
					factory = config.configure().buildSessionFactory();
				}
			}
			return factory;
		}
	}
}

package com.emoto.persistent;

import javax.security.auth.login.Configuration;

import org.hibernate.SessionFactory;

public class PersistentUtils {
	private static SessionFactory factory = null;
	
	@SuppressWarnings("deprecation")
	public static SessionFactory getSessionFactory() {
		if (factory == null) {
			synchronized(PersistentUtils.class) {
				if (factory == null) {
					factory = ((SessionFactory) Configuration.getConfiguration()).getSessionFactory();
				}
			}
		}
		return factory;
	}
}

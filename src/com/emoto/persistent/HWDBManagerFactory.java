package com.emoto.persistent;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import com.emoto.persistent.data.HWId2Barcode;

public class HWDBManagerFactory implements IDBManagerFactory<HWId2Barcode> {
	public SessionFactory factory = null;
	private static Logger logger = Logger.getLogger(HWDBManagerFactory.class.getName());
	
	@Override
	public SessionFactory getSessionFactory() {
		if (factory != null) {
			return factory;
		} else {
			synchronized(this.getClass()) {
				if (factory == null) {
					Configuration config = new Configuration();
					factory = config.configure().buildSessionFactory();
				}
			}
			return factory;
		}
	}
	
	@Override
	public Integer addElement(HWId2Barcode elem) {
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		Integer id = null;
		
		try {
			tx = session.beginTransaction();
			id = (Integer)session.save(elem);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
				e.printStackTrace();
			}
		} catch( PersistenceException  e) {
			if(e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
				logger.log(Level.WARNING,"Error while adding element {0} to DB. Cause is {1}",
						new Object[]{elem, cause.getCause()});
			}
			return null;
		} finally {
			session.close();
		}
		return id;
	}
	
	@Override
	public List<HWId2Barcode> listElement() {
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		
		List elems = null;
		try {
			tx = session.beginTransaction();
			elems = session.createQuery("FROM HWId2Barcode").list();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return elems;
	}
	
	@Override
	public List<HWId2Barcode> getElement(Object index) {
		if (index instanceof String) {
			
		} else {
			logger.log(Level.WARNING, "getElement from DB with wrong type of index " + index);
			return null;
		}
		
		String hwId = (String)index;
		List elems = null;
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			elems = (List)session.find(HWId2Barcode.class,
					"select e.hwId from HWId2Barcode as e where hwId = " + hwId);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return elems;
	}
	
	@Override
	public void updateElement(HWId2Barcode elem) {
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		
		HWId2Barcode elemOut = null;
		try {
			tx = session.beginTransaction();
		    elemOut = (HWId2Barcode)session.get(HWId2Barcode.class, elem.getId());
			elemOut.setBarcode(elem.getBarcode());
			elemOut.setHwId(elem.getHwId());
		    tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		} 
	}
	
	@Override
	public void deleteElement(Object index) {
		if (index instanceof String) {
			
		} else {
			logger.log(Level.WARNING, "deleteElement from DB with wrong type of index " + index);
			return;
		}
		String hwId = (String)index;
		Session session = getSessionFactory().openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			HWId2Barcode elem = (HWId2Barcode)session.get(HWId2Barcode.class, hwId);
			session.delete(elem);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		} 
	}
}

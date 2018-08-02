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

import com.emoto.persistent.data.ChargeRecord;
import com.emoto.persistent.data.HWId2Barcode;
import com.emoto.persistent.data.IDBElement;

public class DBManagerFactory<T extends IDBElement, K> implements IDBManagerFactory<T, K> {
	private static Logger logger = Logger.getLogger(DBManagerFactory.class.getName());
	public SessionFactory factory = null;
	
	public DBManagerFactory(SessionFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public Integer addElement(T elem) {
		Session session = factory.openSession();
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
	public List<T> listElement() {
		Session session = factory.openSession();
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
	public List<T> getElement(K index) {
		if (index instanceof String) {
			
		} else {
			logger.log(Level.WARNING, "getElement from DB with wrong type of index " + index);
			return null;
		}
		
		String hwId = (String)index;
		List elems = null;
		Session session = factory.openSession();
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
	public void updateElement(T elem) {
		Session session = factory.openSession();
		Transaction tx = null;
		
		T elemOut = null;
		try {
			tx = session.beginTransaction();
		    elemOut = (T)session.get(elem.getClass(), elem.getId());
		    elemOut.copyFrom(elem);
//			elemOut.setHwId(elem.getHwId());
//			elemOut.setChargeId(elem.getChargeId());
//			elemOut.setPortId(elem.getPortId());
//			elemOut.setMeter(elem.getMeter());
//			elemOut.setVoltage(elem.getVoltage());
//			elemOut.setCurrency(elem.getCurrency());
//			elemOut.setSessionId(elem.getSessionId());
//			elemOut.setStartTime(elem.getStartTime());
//			elemOut.setEndTime(elem.getEndTime());
//			elemOut.setEffectiveTime(elem.getEffectiveTime());
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
	public void deleteElement(K index) {
		if (index instanceof String) {
			
		} else {
			logger.log(Level.WARNING, "deleteElement from DB with wrong type of index " + index);
			return;
		}
		String hwId = (String)index;
		Session session = factory.openSession();
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

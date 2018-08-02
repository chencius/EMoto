package com.emoto.persistent;

import java.util.List;

import org.hibernate.SessionFactory;

import com.emoto.persistent.data.IDBElement;

public interface IDBManagerFactory<T extends IDBElement, K> {
	public Integer addElement(T elem);
	public List<T> listElement();
	public List<T> getElement(K index);
	public void updateElement(T elem);
	public void deleteElement(K index);	
}

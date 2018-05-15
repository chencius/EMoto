package com.emoto.server;

import java.util.Iterator;
import java.util.List;

import com.emoto.persistent.HWDBManagerFactory;
import com.emoto.persistent.data.HWId2Barcode;


public class Client2 {

	public static void main(String[] args) {
		HWDBManagerFactory factory = new HWDBManagerFactory();
		HWId2Barcode elem = new HWId2Barcode();
		elem.setBarcode("barcode11");
		elem.setHwId("hwId1");
		factory.addElement(elem);
		
		elem.setBarcode("barcode22");
		elem.setHwId("hwId22");
		factory.addElement(elem);
		
		List l = factory.listElement();
		
		Iterator itr = l.iterator();
		while  ( itr.hasNext() ) {
			System.out.println(itr.next());
		}
	}

}

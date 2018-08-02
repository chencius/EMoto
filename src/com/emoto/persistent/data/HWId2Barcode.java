package com.emoto.persistent.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hwId2Barcode")
public class HWId2Barcode implements IDBElement {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "hwId")
	private String hwId;
	
	@Column(name = "barcode")
	private String barcode;

	@Override
	public int getId() {
		return id;
	}

	public String getHwId() {
		return hwId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setHwId(String hwId) {
		this.hwId = hwId;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	public void copyFrom(IDBElement e) {
		HWId2Barcode elem = (HWId2Barcode)e;
		
		this.id = elem.getId();
	}
	
	public String toString() {
		return "Id = " + id + " hwId = " + hwId + " barcode= " + barcode;
	}
}

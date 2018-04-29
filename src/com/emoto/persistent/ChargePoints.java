package com.emoto.persistent;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "charge_point", uniqueConstraints = {@UniqueConstraint(columnNames = {"chargepoint_id"})})
public class ChargePoints {
	@Id
	@GeneratedValue
	@Column(name="id")
	private Integer id;
	
	@Column(name="chargepoint_id", length=20, nullable=false)
	private String chargePointId;
	
	@Column(name="port_no")
	private byte portNo;
	
	@OneToOne(cascade=CascadeType.ALL)
	private ChargePortStatus portStatus;

	public ChargePoints() {
		
	}
	
	public ChargePoints(Integer id, String chargePointId, byte portNo) {
		super();
		this.id = id;
		this.chargePointId = chargePointId;
		this.portNo = portNo;
	}

	public String getChargePointId() {
		return chargePointId;
	}

	public byte getPortNo() {
		return portNo;
	}

	public ChargePortStatus getPortStatus() {
		return portStatus;
	}

	public void setChargePointId(String chargePointId) {
		this.chargePointId = chargePointId;
	}

	public void setPortNo(byte portNo) {
		this.portNo = portNo;
	}

	public void setPortStatus(ChargePortStatus portStatus) {
		this.portStatus = portStatus;
	}
}

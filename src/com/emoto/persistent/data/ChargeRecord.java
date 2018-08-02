package com.emoto.persistent.data;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chargeRecord")
public class ChargeRecord implements IDBElement {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "hwId")
	private String hwId;
	
	@Column(name = "chargeId")
	private long chargeId;
	
	@Column(name = "portId")
	private byte portId;
	
	@Column(name = "meter")
	private float meter;
	
	@Column(name = "voltage")
	private float voltage;
	
	@Column(name = "currency")
	private float currency;
	
	@Column(name = "sessionId")
	private long sessionId;
	
	@Column(name = "startTime")
	private Timestamp startTime;
	
	@Column(name = "endTime")
	private Timestamp endTime;
	
	@Column(name = "effectiveTime")
	private Timestamp effectiveTime;

	@Override
	public int getId() {
		return id;
	}
	
	public String getHwId() {
		return hwId;
	}

	public long getChargeId() {
		return chargeId;
	}

	public byte getPortId() {
		return portId;
	}

	public float getMeter() {
		return meter;
	}

	public float getVoltage() {
		return voltage;
	}

	public float getCurrency() {
		return currency;
	}

	public long getSessionId() {
		return sessionId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public Timestamp getEffectiveTime() {
		return effectiveTime;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setHwId(String hwId) {
		this.hwId = hwId;
	}

	public void setChargeId(long chargeId) {
		this.chargeId = chargeId;
	}

	public void setPortId(byte portId) {
		this.portId = portId;
	}

	public void setMeter(float meter) {
		this.meter = meter;
	}

	public void setVoltage(float voltage) {
		this.voltage = voltage;
	}

	public void setCurrency(float currency) {
		this.currency = currency;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public void setEffectiveTime(Timestamp effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	public void copyFrom(IDBElement e) {
		ChargeRecord elem = (ChargeRecord)e;
		this.id = elem.getId();
		this.hwId = elem.getHwId();
		this.chargeId = elem.getChargeId();
		this.portId = elem.getPortId();
		this.meter = elem.getMeter();
		this.voltage = elem.getVoltage();
		this.currency = elem.getCurrency();
		this.sessionId = elem.getSessionId();
		this.startTime = elem.getStartTime();
		this.endTime = elem.getEndTime();
		this.effectiveTime = elem.getEffectiveTime();
	}
	
	public String toString() {
		return "id = " + id + " hwId = " + hwId + " chargeId = " + chargeId + " portId = " + portId + " meter = " + meter
				+ " voltage = " + voltage + " currency = " + currency + " sessionId = " + sessionId + " startTime = " + startTime
				+ " endTime = " + endTime + " effectiveTime = " + effectiveTime;
	}
}

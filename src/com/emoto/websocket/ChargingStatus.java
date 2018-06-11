package com.emoto.websocket;

public class ChargingStatus {
	private String func;
	private long chargeId;
	private byte portId;
	private boolean status;
	private float meter;
	private float voltage;
	private float currency;
	private int estimatedTime;

	public ChargingStatus(String func, long chargeId, byte portId, boolean status, float meter, float voltage,
			float currency, int estimatedTime) {
		super();
		this.func = func;
		this.chargeId = chargeId;
		this.portId = portId;
		this.status = status;
		this.meter = meter;
		this.voltage = voltage;
		this.currency = currency;
		this.estimatedTime = estimatedTime;
	}
	
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public long getChargeId() {
		return chargeId;
	}
	public byte getPortId() {
		return portId;
	}
	public boolean getStatus() {
		return status;
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
	public int getEstimatedTime() {
		return estimatedTime;
	}

	public void setChargeId(long chargeId) {
		this.chargeId = chargeId;
	}
	public void setPortId(byte portId) {
		this.portId = portId;
	}
	public void setStatus(boolean status) {
		this.status = status;
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
	
	public void setEstimatedTime(int estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
}

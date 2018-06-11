package com.emoto.websocket;

public class ValueReturned implements IValueReturned {
	private long chargeId;
	private byte portId;
	private boolean status;
	private String reason;
	private float meterValue;
	private float batteryEnergy;
	private float voltage;
	private float currency;
	private int estimatedTime;

	public ValueReturned() {
		this.status = false;
		this.reason = "Default error";
	}
	
	@Override
	public long getChargeId() {
		return this.chargeId;
	}
	
	@Override
	public void setChargeId(long chargeId) {
		this.chargeId = chargeId;
	}
	
	@Override
	public byte getPortId() {
		return this.portId;
	}
	
	@Override
	public void setPortId(byte portId) {
		this.portId = portId;
	}
	
	@Override
	public boolean getStatus() {
		return this.status;
	}
	
	@Override
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	@Override
	public String getReason() {
		return reason;
	}
	
	@Override
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Override
	public float getMeterValue() {
		return meterValue;
	}
	
	@Override
	public void setMeterValue(float meterValue) {
		this.meterValue = meterValue;
	}
	
	@Override
	public float getBatteryEnergy() {
		return batteryEnergy;
	}
	
	@Override
	public void setBatteryEnergy(float batteryEnergy) {
		this.batteryEnergy = batteryEnergy;
	}
	
	@Override
	public float getVoltage() {
		return voltage;
	}
	
	@Override
	public void setVoltage(float voltage) {
		this.voltage = voltage;
	}
	
	@Override
	public float getCurrency() {
		return this.currency;
	}
	
	@Override
	public void setCurrency(float currency) {
		this.currency = currency;
	}
	
	@Override
	public int getEstimatedTime() {
		return estimatedTime;
	}
	
	@Override
	public void setEstimatedTime(int estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	
	@Override
	public String toString() {
		return String.format(
			"chargeId=%d, portId=%d, status=%b, reason=%s, meterValue=%f, batteryEnergy=%f, voltage=%f, currency=%f, estimatedTime=%d",
			chargeId, portId, status, reason, meterValue, batteryEnergy, voltage, currency, estimatedTime);
	}
}

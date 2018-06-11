package com.emoto.websocket;

public interface IValueReturned {
	public long getChargeId();
	public void setChargeId(long chargeId);
	public byte getPortId();
	public void setPortId(byte portId);
	public boolean getStatus();
	public void setStatus(boolean status);
	public String getReason();
	public void setReason(String reason);
	public float getMeterValue();
	public void setMeterValue(float meterValue);
	public float getBatteryEnergy();
	public void setBatteryEnergy(float batteryEnergy);
	public float getVoltage();
	public void setVoltage(float voltage);
	public float getCurrency();
	public void setCurrency(float currency);
	public int getEstimatedTime();
	public void setEstimatedTime(int time);
}

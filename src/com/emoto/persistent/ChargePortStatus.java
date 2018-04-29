package com.emoto.persistent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="charge_port_status")
public class ChargePortStatus {
	@Id
	@GeneratedValue
	@Column(name="id")
	private Integer id;
	
	@Column(name="chargepoint_id", length=20, nullable=false)
	private String chargePointId;
	
	@Column(name="port_no")
	private byte portNo;
	
	@Column(name="session_id")
	private long sessionId;
	
	@Column(name="status")
	private byte status;
	
	@Column(name="error_code")
	private byte errorCode;
	
	@Column(name="meter_value")
	private int meterValue;
	
	@Column(name="battery_energy")
	private byte batteryEnergy;
	
	@Column(name="charging_voltage")
	private short chargingVoltage;
	
	@Column(name="charging_currency")
	private short chargingCurrency;
	
	@Column(name="estimated_finish_time")
	private byte estimatedFinishTime;
	
	@Column(name="source")
	private byte source;

	public ChargePortStatus() {
		
	}
	
	public ChargePortStatus(String chargePointId, byte portNo, long sessionId, byte status, byte errorCode,
			int meterValue, byte batteryEnergy, short chargingVoltage, short chargingCurrency, byte estimatedFinishTime,
			byte source) {
		super();
		this.chargePointId = chargePointId;
		this.portNo = portNo;
		this.sessionId = sessionId;
		this.status = status;
		this.errorCode = errorCode;
		this.meterValue = meterValue;
		this.batteryEnergy = batteryEnergy;
		this.chargingVoltage = chargingVoltage;
		this.chargingCurrency = chargingCurrency;
		this.estimatedFinishTime = estimatedFinishTime;
		this.source = source;
	}

	public Integer getId() {
		return id;
	}

	public String getChargePointId() {
		return chargePointId;
	}

	public byte getPortNo() {
		return portNo;
	}

	public long getSessionId() {
		return sessionId;
	}

	public byte getStatus() {
		return status;
	}

	public byte getErrorCode() {
		return errorCode;
	}

	public int getMeterValue() {
		return meterValue;
	}

	public byte getBatteryEnergy() {
		return batteryEnergy;
	}

	public short getChargingVoltage() {
		return chargingVoltage;
	}

	public short getChargingCurrency() {
		return chargingCurrency;
	}

	public byte getEstimatedFinishTime() {
		return estimatedFinishTime;
	}

	public byte getSource() {
		return source;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setChargePointId(String chargePointId) {
		this.chargePointId = chargePointId;
	}

	public void setPortNo(byte portNo) {
		this.portNo = portNo;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public void setErrorCode(byte errorCode) {
		this.errorCode = errorCode;
	}

	public void setMeterValue(int meterValue) {
		this.meterValue = meterValue;
	}

	public void setBatteryEnergy(byte batteryEnergy) {
		this.batteryEnergy = batteryEnergy;
	}

	public void setChargingVoltage(short chargingVoltage) {
		this.chargingVoltage = chargingVoltage;
	}

	public void setChargingCurrency(short chargingCurrency) {
		this.chargingCurrency = chargingCurrency;
	}

	public void setEstimatedFinishTime(byte estimatedFinishTime) {
		this.estimatedFinishTime = estimatedFinishTime;
	}

	public void setSource(byte source) {
		this.source = source;
	}
}

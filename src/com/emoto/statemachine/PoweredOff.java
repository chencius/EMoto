package com.emoto.statemachine;

import java.util.logging.Level;

public class PoweredOff implements State{
	private ChargePort cp;
	
	public PoweredOff(ChargePort cp) {
		this.cp = cp;
	}
	
	@Override
	public void powerOn() {
		cp.setState(cp.availableState);
		logger.log(Level.INFO, "CP state changes from PoweredOff to Available");
	}

	@Override
	public void connect() {
		logger.log(Level.WARNING, "Invalid action connect with CP state = PoweredOff");
	}

	@Override
	public void startCharge() {
		logger.log(Level.WARNING, "Invalid action startCharge with CP state = PoweredOff");
	}

	@Override
	public void stopCharge() {
		logger.log(Level.WARNING, "Invalid action stopCharge with CP state = PoweredOff");
	}

	@Override
	public void disconnect() {
		logger.log(Level.WARNING, "Invalid action disconnect with CP state = PoweredOff");
	}


}

package org.ops4j.mpjp.test.model;

import javax.json.bind.annotation.JsonbCreator;

public class Carrier<P extends Pet> {
	public enum CarrierType {
		BAG, CRATE, TROLLEY
	}

	private CarrierType carrierType;
	private P carriedPet;

	@JsonbCreator
	public Carrier(CarrierType carrierType, P carriedPet) {
		this.carrierType = carrierType;
		this.carriedPet = carriedPet;
	}

	public CarrierType getCarrierType() {
		return carrierType;
	}

	public void setCarrierType(CarrierType carrierType) {
		this.carrierType = carrierType;
	}

	public P getCarriedPet() {
		return carriedPet;
	}

	public void setCarriedPet(P carriedPet) {
		this.carriedPet = carriedPet;
	}
}
package org.ops4j.mpjp.test.model;

public class MarriedCouple {
	private Person husband;
	private Person wife;
	
	public MarriedCouple() {
	}

	public MarriedCouple(Person husband, Person wife) {
		this.husband = husband;
		this.wife = wife;
	}

	public Person getHusband() {
		return husband;
	}

	public void setHusband(Person husband) {
		this.husband = husband;
	}

	public Person getWife() {
		return wife;
	}

	public void setWife(Person wife) {
		this.wife = wife;
	}
}

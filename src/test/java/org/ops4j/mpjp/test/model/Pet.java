package org.ops4j.mpjp.test.model;

public abstract class Pet {
	private String name;
	private int age;
	private Boolean furry;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Boolean getFurry() {
		return furry;
	}

	public void setFurry(Boolean furry) {
		this.furry = furry;
	}

}
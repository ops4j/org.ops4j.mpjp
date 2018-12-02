package org.ops4j.mpjp.test.model;

import java.time.LocalDate;

public class Member extends Person{

	private LocalDate since;

    public Member() {
    }

    public Member(String firstName, String lastName, LocalDate since) {
    	super(firstName, lastName);
    	this.since = since;
    }

	public LocalDate getSince() {
		return since;
	}

	public void setSince(LocalDate since) {
		this.since = since;
	}
}
package org.springframework.webflow.support.persistence;

public class TestBean {

	private long entityId;

	private String name;

	public TestBean(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

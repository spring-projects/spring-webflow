/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.persistence;

import java.util.HashSet;
import java.util.Set;

public class TestBean {

	private Long entityId;

	private String name;

	private Set<TestAddress> addresses = new HashSet<TestAddress>();

	private int count;

	public TestBean() {

	}

	public TestBean(String name) {
		this.name = name;
	}

	public TestBean(long id, String name) {
		this.entityId = new Long(id);
		this.name = name;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<TestAddress> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<TestAddress> addresses) {
		this.addresses = addresses;
	}

	public int getCount() {
		return count;
	}

	public void incrementCount() {
		this.count++;
	}

}

/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.samples.phonebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {

	private Long id;

	private String firstName;

	private String lastName;

	private String userId;

	private String phone;

	private List<Person> colleagues = new ArrayList<Person>();

	public Person() {
		this(-1, "", "", "", "");
	}

	public Person(long id, String firstName, String lastName, String userId, String phone) {
		this.id = new Long(id);
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId;
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getUserId() {
		return userId;
	}

	public String getPhone() {
		return this.phone;
	}

	public List getColleagues() {
		return this.colleagues;
	}

	public int getColleagueCount() {
		return this.colleagues.size();
	}

	public Person getColleague(int i) {
		return this.colleagues.get(i);
	}

	public void addColleague(Person colleague) {
		this.colleagues.add(colleague);
	}
}
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
package org.springframework.webflow;

import java.io.Serializable;

/**
 * Simple test bean used by some test cases. Note that this bean has value semantics.
 */
public class TestBean implements Serializable {

	public String datum1 = "";

	public int datum2;

	public boolean executed;

	public void execute() {
		this.executed = true;
	}

	public String getDatum1() {
		return datum1;
	}

	public int getDatum2() {
		return datum2;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void execute(String parameter) {
		this.executed = true;
		this.datum1 = parameter;
	}

	public int execute(String parameter, int parameter2) {
		this.executed = true;
		this.datum1 = parameter;
		this.datum2 = parameter2;
		return datum2;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof TestBean)) {
			return false;
		}
		TestBean other = (TestBean) obj;
		return datum1.equals(other.datum1) && datum2 == other.datum2 && executed == other.executed;
	}

	public int hashCode() {
		return (datum1.hashCode() + datum2 + (executed ? 1 : 0)) * 29;
	}
}
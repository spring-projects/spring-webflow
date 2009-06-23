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
package org.springframework.webflow.core.collection;

import java.io.Serializable;

/**
 * Test bean used in unit tests.
 */
public class TestBean implements Serializable {

	private int amount = 0;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean equals(Object o) {
		if (!(o instanceof TestBean)) {
			return false;
		}
		return amount == ((TestBean) o).amount;
	}

	public int hashCode() {
		return amount * 29;
	}

	public String toString() {
		return "[TestBean amount = " + amount + "]";
	}
}
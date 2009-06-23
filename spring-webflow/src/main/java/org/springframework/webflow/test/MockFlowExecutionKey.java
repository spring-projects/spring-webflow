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
package org.springframework.webflow.test;

import org.springframework.webflow.execution.FlowExecutionKey;

/**
 * A simple flow execution key implementation. New instances of this class get their values from a sequence encapsulated
 * as a static private variable of this class.
 * 
 * @author Keith Donald
 */
public class MockFlowExecutionKey extends FlowExecutionKey {

	private String value;

	/**
	 * Creates a new mock flow execution key.
	 */
	public MockFlowExecutionKey(String value) {
		this.value = value;
	}

	public boolean equals(Object o) {
		if (!(o instanceof MockFlowExecutionKey)) {
			return false;
		}
		MockFlowExecutionKey key = (MockFlowExecutionKey) o;
		return value.equals(key.value);
	}

	public int hashCode() {
		return value.hashCode();
	}

	public String toString() {
		return value;
	}
}
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
package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Result;
import org.springframework.core.style.ToStringCreator;

/**
 * Indicates a successful mapping operation.
 * @author Keith Donald
 */
public class Success extends Result {

	private Object mappedValue;

	private Object originalValue;

	/**
	 * Creates a new success result.
	 * @param mappedValue the successfully mapped value
	 * @param originalValue the original value
	 */
	public Success(Object mappedValue, Object originalValue) {
		this.mappedValue = mappedValue;
		this.originalValue = originalValue;
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	public Object getMappedValue() {
		return mappedValue;
	}

	public boolean isError() {
		return false;
	}

	public String getErrorCode() {
		return null;
	}

	public String toString() {
		return new ToStringCreator(this).append("originalValue", originalValue).append("mappedValue", mappedValue)
				.toString();
	}
}

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
 * The "required" error result--indicates a required mapping could not be performed because the source value to map was
 * empty.
 * @author Keith Donald
 */
public class RequiredError extends Result {

	private Object originalValue;

	/**
	 * Creates a new required error result
	 * @param originalValue the original source value (empty)
	 */
	public RequiredError(Object originalValue) {
		this.originalValue = originalValue;
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	public Object getMappedValue() {
		return null;
	}

	public boolean isError() {
		return true;
	}

	public String getErrorCode() {
		return "required";
	}

	public String toString() {
		return new ToStringCreator(this).append("originalValue", originalValue).toString();
	}
}

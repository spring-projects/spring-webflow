/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.mapping.results;

import org.springframework.binding.mapping.Mapping;

/**
 * Indicates a type conversion occurred during a mapping operation.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class TypeConversionError extends AbstractMappingResult {

	private Object originalValue;

	private Exception cause;

	/**
	 * Creates a new type conversion error.
	 * @param cause the underlying type conversion exception
	 */
	public TypeConversionError(Mapping mapping, Object originalValue, Exception cause) {
		super(mapping);
		this.originalValue = originalValue;
		this.cause = cause;
	}

	public String getCode() {
		return "typeMismatch";
	}

	public boolean isError() {
		return true;
	}

	public Throwable getErrorCause() {
		return cause;
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	public Object getMappedValue() {
		return null;
	}

}

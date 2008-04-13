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
package org.springframework.binding.mapping;

/**
 * Exposes information about the result of a single mapping operation.
 * @author Keith Donald
 */
public abstract class Result {

	/**
	 * The original value of the source object that was to be mapped. Null if this result is an error on the source
	 * object.
	 */
	public abstract Object getOriginalValue();

	/**
	 * The actual value that was mapped to the target object. Null if this result is an error.
	 */
	public abstract Object getMappedValue();

	/**
	 * Indicates if this result was an error.
	 */
	public abstract boolean isError();

	/**
	 * If this result was an error, the logical mapping error code; for example "propertyNotFound". Null if this result
	 * is not an error result.
	 */
	public abstract String getErrorCode();
}

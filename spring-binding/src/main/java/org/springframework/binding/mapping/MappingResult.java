/*
 * Copyright 2004-2017 the original author or authors.
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

import java.io.Serializable;

/**
 * A single data mapping result. Each result has a unique {@link #getCode() code}, and provides context about the result
 * of a single data mapping operation.
 * 
 * @author Keith Donald
 */
public interface MappingResult extends Serializable {

	/**
	 * The mapping that executed for which this result pertains to.
	 */
	Mapping getMapping();

	/**
	 * The mapping result code; for example, "success" , "typeMismatch", "propertyNotFound", or "evaluationException".
	 */
	String getCode();

	/**
	 * Indicates if this result is an error result.
	 */
	boolean isError();

	/**
	 * Get the cause of the error result
	 * @return the underyling cause, or null if this is not an error or there was no root cause.
	 */
	Throwable getErrorCause();

	/**
	 * The original value of the source object that was to be mapped. May be null if this result is an error on the
	 * source object.
	 */
	Object getOriginalValue();

	/**
	 * The actual value that was mapped to the target object. Null if this result is an error.
	 */
	Object getMappedValue();

}
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

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.mapping.Mapping;

/**
 * Indicates an exception occurred accessing the source object to be mapped. Used to report source
 * {@link PropertyNotFoundException} errors and general {@link EvaluationException} errors.
 * @author Keith Donald
 */
public class SourceAccessError extends AbstractMappingResult {

	private EvaluationException cause;

	/**
	 * Creates a new source access error.
	 * @param error the underlying evaluation exception that occurred
	 */
	public SourceAccessError(Mapping mapping, EvaluationException error) {
		super(mapping);
		this.cause = error;
	}

	public String getCode() {
		if (cause instanceof PropertyNotFoundException) {
			return "propertyNotFound";
		} else {
			return "evaluationException";
		}
	}

	public boolean isError() {
		return true;
	}

	public Throwable getErrorCause() {
		return cause;
	}

	public Object getOriginalValue() {
		return null;
	}

	public Object getMappedValue() {
		return null;
	}

}

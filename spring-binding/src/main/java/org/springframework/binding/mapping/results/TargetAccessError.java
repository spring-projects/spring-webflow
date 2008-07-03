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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.PropertyNotFoundException;
import org.springframework.binding.mapping.Result;
import org.springframework.core.style.ToStringCreator;

/**
 * Indicates an exception occurred accessing the target object to be mapped to. Used to report source
 * {@link PropertyNotFoundException} errors and general {@link EvaluationException} errors.
 * @author Keith Donald
 */
public class TargetAccessError extends Result {

	private Object originalValue;

	private EvaluationException error;

	/**
	 * Creates a new target access error.
	 * @param originalValue the value that was attempted to be mapped
	 * @param error the underlying evaluation exception that occurred
	 */
	public TargetAccessError(Object originalValue, EvaluationException error) {
		this.originalValue = originalValue;
		this.error = error;
	}

	/**
	 * Returns the backing target evaluation exception that occurred.
	 */
	public EvaluationException getException() {
		return error;
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
		if (error instanceof PropertyNotFoundException) {
			return "propertyNotFound";
		} else {
			return "evaluationException";
		}
	}

	public String toString() {
		ToStringCreator creator = new ToStringCreator(this).append("errorCode", getErrorCode());
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		error.printStackTrace(writer);
		creator.append("stackTrace", stringWriter.toString());
		return creator.toString();
	}
}

/*
 * Copyright 2008 the original author or authors.
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
package org.springframework.binding.validation;

import java.util.Collections;
import java.util.Map;

import org.springframework.binding.message.Severity;
import org.springframework.util.Assert;

/**
 * An indication of a validation failure. A failure is generated when a validation constraint is violated; for example
 * "required", "length", "invalidFormat". A failure has a severity describing the intensity of the violation. A failure
 * may have an explicit message summarizing what went wrong, and may also provide additional details, such as a cause or
 * suggested recovery action. A failure may also have additional arguments that can be used as named parameters in any
 * UI display messages.
 */
public class ValidationFailure {

	private String property;

	private String constraint;

	private Severity severity;

	private String message;

	private Map details;

	private Map arguments;

	/**
	 * Creates a new validation failure
	 * @param severity the severity of the failure (required)
	 * @param property the property that failed to validate (may be null)
	 * @param constraint the name of the validation constraint that failed (may be null)
	 * @param message an explicit failure message (may be null)
	 * @param details additional failure details (may be null)
	 * @param arguments named failure arguments (may be null)
	 */
	public ValidationFailure(Severity severity, String property, String constraint, String message, Map details,
			Map arguments) {
		Assert.notNull(severity, "The severity is required");
		this.property = property;
		this.constraint = constraint;
		this.message = message;
		this.details = details != null ? Collections.unmodifiableMap(details) : Collections.EMPTY_MAP;
		this.arguments = arguments != null ? Collections.unmodifiableMap(arguments) : Collections.EMPTY_MAP;
	}

	/**
	 * The name of the property that failed to validate. May be null to indicate a failure against the currently
	 * validating object.
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * The name of the validation constraint that caused this failure to be reported. This constraint name can be used
	 * to resolve the failure message if no explicit {@link #getMessage()} is configured. May be null to indicate a
	 * failure against the currently validating constraint.
	 */
	public String getConstraint() {
		return constraint;
	}

	/**
	 * The severity of the failure, which measures the impact of the constraint violation.
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * The message summarizing this failure. May be a literal string or a resolvable message code. Can be null. If null,
	 * the failure message will be resolved using the constraint that was being validated when this failure was
	 * reported.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * A map of details providing additional information about this failure. Each entry in this map is a failure detail
	 * item that has a name and value. The name uniquely identifies the failure detail and describes its purpose; for
	 * example, a "cause" or "recommendedAction". The value is the failure detail message, either a literal string or
	 * resolvable code. If resolvable, the detail code is relative to the constraint associated with this failure.
	 * Returns an empty map if no details are present.
	 */
	public Map getDetails() {
		return details;
	}

	/**
	 * An map of arguments that can be used as named parameters in resolvable messages associated with this validation
	 * failure. Each constraint that can fail defines a set of arguments that are specific to it. For example, a length
	 * constraint might define arguments of "min" and "max" of Integer values. In the message bundle, you then might see
	 * "length=The ${label} field value must be between ${min} and ${max}". Returns an empty map if no arguments are
	 * present.
	 */
	public Map getArguments() {
		return arguments;
	}

}

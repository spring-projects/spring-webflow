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
package org.springframework.binding.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.binding.message.Severity;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * A builder that provides a fluent interface for configuring a Validation failure. Example:
 * 
 * <pre>
 * new ValidationFailureBuilder().forProperty(&quot;foo&quot;).constraint(&quot;required&quot;).build();
 * </pre>
 * @author Keith Donald
 * 
 */
public class ValidationFailureBuilder {

	private String property;

	private String constraint;

	private Severity severity;

	private Map args;

	private String message;

	private Map details;

	/**
	 * Sets the failure to be of warning severity.
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder warning() {
		severity = Severity.WARNING;
		return this;
	}

	/**
	 * Sets the failure to be of error severity. This is the default Severity.
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder error() {
		severity = Severity.ERROR;
		return this;
	}

	/**
	 * Sets the property the failure occurred against.
	 * @param property the property name
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder forProperty(String property) {
		this.property = property;
		return this;
	}

	/**
	 * Sets the name of the constraint that failed.
	 * @param constraint the name of the validation constraint
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder constraint(String constraint) {
		this.constraint = constraint;
		return this;
	}

	/**
	 * Sets an explicit failure message. The value may be a literal string or a resolvable message code.
	 * @param message the failure message
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder message(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Add a detail to associate with the failure. The value provided serves as both the logical name of the detail and
	 * the code used to resolve the detail message text.
	 * @param nameAndValue the value to use as both the logical name of the message and the constraint-relative message
	 * code; for example, "cause" or "recommendedAction".
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder detail(String nameAndValue) {
		return detail(nameAndValue, nameAndValue);
	}

	/**
	 * Add a detail to associate with the failure.
	 * @param name the logical name of the message; for example, "cause" or "recommendedAction"
	 * @param value the detail value, either a hard coded message string or a constraint-relative message code used to
	 * resolve the detail message text
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder detail(String name, String value) {
		if (details == null) {
			details = new HashMap();
		}
		details.put(name, value);
		return this;
	}

	/**
	 * Adds a failure message argument.
	 * @param name the argument name
	 * @param value the argument value
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder arg(String name, Object value) {
		if (args == null) {
			args = new TreeMap();
		}
		args.put(name, value);
		return this;
	}

	/**
	 * Adds a failure message argument whose value is also message source resolvable. Use this when the argument value
	 * itself needs to be localized.
	 * @param name the argument name
	 * @param code the code that will be used to resolve the argument
	 * @see MessageSourceResolvable
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder resolvableArg(String name, String code) {
		return arg(name, new DefaultMessageSourceResolvable(code));
	}

	/**
	 * Build the ValidationFailure. Call after setting builder properties.
	 * @see #forProperty(String)
	 * @see #constraint(String)
	 * @see #message(String)
	 * @see #detail(String)
	 * @see #arg(String, Object)
	 * @see #resolvableArg(String, String)
	 * @return this, for fluent call chaining
	 */
	public ValidationFailure build() {
		if (severity == null) {
			severity = Severity.ERROR;
		}
		return new ValidationFailure(severity, property, constraint, message, details, args);
	}

}
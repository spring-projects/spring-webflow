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

	private String defaultText;

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
	 * Sets the failure to be of warning severity.
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder warning() {
		severity = Severity.WARNING;
		return this;
	}

	/**
	 * Sets the failure to be of error severity.
	 * @return this, for fluent call chaining
	 */
	public ValidationFailureBuilder error() {
		severity = Severity.ERROR;
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
	 * Build the ValidationFailure. Called after setting builder properties.
	 * @return this, for fluent call chaining
	 */
	public ValidationFailure build() {
		if (severity == null) {
			severity = Severity.ERROR;
		}
		return new ValidationFailure(property, constraint, severity, args, defaultText);
	}

}
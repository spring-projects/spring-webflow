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
import org.springframework.util.StringUtils;

public class ValidationFailureBuilder {

	private String propertyName;

	private String constraint;

	private Severity severity;

	private Map args = new TreeMap();

	private String defaultText;

	public ValidationFailureBuilder forProperty(String propertyName) {
		this.propertyName = propertyName;
		return this;
	}

	public ValidationFailureBuilder constraint(String constraint) {
		this.constraint = constraint;
		return this;
	}

	public ValidationFailureBuilder warning() {
		severity = Severity.WARNING;
		return this;
	}

	public ValidationFailureBuilder error() {
		severity = Severity.ERROR;
		return this;
	}

	public ValidationFailureBuilder arg(String name, Object value) {
		args.put(name, value);
		return this;
	}

	public ValidationFailure build() {
		if (severity == null) {
			severity = Severity.ERROR;
		}
		if (StringUtils.hasText(propertyName)) {
			return new PropertyValidationFailure(propertyName, constraint, severity, args, defaultText);
		} else {
			return new ValidationFailure(constraint, severity, args, defaultText);
		}
	}

}

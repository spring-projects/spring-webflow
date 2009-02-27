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

/**
 * Provides additional model context regarding a validation failure. Used by a
 * {@link ValidationFailureMessageResolverFactory} to resolve failure messages.
 */
public class ValidationFailureModelContext {

	private String model;

	private Object invalidValue;

	private Class propertyType;

	private String propertyConverter;

	/**
	 * Creates a new validation model context.
	 * @param model the name of the model object that was validated
	 * @param invalidValue the invalid value the user entered
	 * @param propertyType the type of the property that failed to validate (may be null)
	 * @param propertyConverter the id of the custom converter configured to format the property value (may be null)
	 */
	public ValidationFailureModelContext(String model, Object invalidValue, Class propertyType, String propertyConverter) {
		this.model = model;
		this.invalidValue = invalidValue;
		this.propertyType = propertyType;
		this.propertyConverter = propertyConverter;
	}

	/**
	 * The name of the model object that was validated.
	 */
	public String getModel() {
		return model;
	}

	/**
	 * When reporting a property validation failure, the invalid user entered value.
	 */
	public Object getInvalidValue() {
		return invalidValue;
	}

	/**
	 * When reporting a property validation failure, the type of the property that failed to validate.
	 */
	public Class getPropertyType() {
		return propertyType;
	}

	/**
	 * When reporting a property validation failure, the id of the custom converter used to format the UI display value.
	 */
	public String getPropertyConverter() {
		return propertyConverter;
	}

}

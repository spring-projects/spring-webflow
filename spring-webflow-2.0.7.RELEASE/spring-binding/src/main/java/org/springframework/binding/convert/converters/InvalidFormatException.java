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
package org.springframework.binding.convert.converters;

/**
 * Thrown when a formatted value is of the wrong form.
 * 
 * @author Keith Donald
 */
public class InvalidFormatException extends RuntimeException {

	private String invalidValue;

	private String expectedFormat;

	/**
	 * Create a new invalid format exception.
	 * @param invalidValue the invalid value
	 * @param expectedFormat the expected format
	 */
	public InvalidFormatException(String invalidValue, String expectedFormat) {
		this(invalidValue, expectedFormat, null);
	}

	/**
	 * Create a new invalid format exception.
	 * @param invalidValue the invalid value
	 * @param expectedFormat the expected format
	 * @param cause the underlying cause of this exception
	 */
	public InvalidFormatException(String invalidValue, String expectedFormat, Throwable cause) {
		super("Invalid format for value '" + invalidValue + "'; the expected format was '" + expectedFormat + "'",
				cause);
		this.invalidValue = invalidValue;
		this.expectedFormat = expectedFormat;
	}

	/**
	 * Create a new invalid format exception.
	 * @param invalidValue the invalid value
	 * @param expectedFormat the expected format
	 * @param message a descriptive message
	 * @param cause the underlying cause of this exception
	 */
	public InvalidFormatException(String invalidValue, String expectedFormat, String message, Throwable cause) {
		super(message, cause);
		this.invalidValue = invalidValue;
		this.expectedFormat = expectedFormat;
	}

	/**
	 * Returns the invalid value.
	 */
	public String getInvalidValue() {
		return invalidValue;
	}

	/**
	 * Returns the expected format.
	 */
	public String getExpectedFormat() {
		return expectedFormat;
	}
}
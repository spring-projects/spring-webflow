/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.binding.format;

/**
 * A lightweight interface for formatting a value and parsing a value from its formatted form.
 * 
 * @author Keith Donald
 */
public interface Formatter {

	/**
	 * Format the value.
	 * @param value the value to format
	 * @return the formatted string, fit for display in a UI
	 * @throws IllegalArgumentException the value could not be formatted
	 */
	public String formatValue(Object value) throws IllegalArgumentException;

	/**
	 * Parse the formatted string representation of a value, restoring the value.
	 * @param formattedString the formatted string representation
	 * @param targetClass the target class to convert the formatted value to
	 * @return the parsed value
	 * @throws InvalidFormatException the string was in an invalid form
	 */
	public Object parseValue(String formattedString, Class targetClass) throws InvalidFormatException;

}
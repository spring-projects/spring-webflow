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
 * Formats objects for display.
 * 
 * @author Keith Donald
 */
public interface Formatter {

	/**
	 * Format the object for display.
	 * @param object the object to format
	 * @return the formatted string, fit for display in a UI
	 * @throws IllegalArgumentException if the object could not be formatted
	 */
	public String format(Object object) throws IllegalArgumentException;

	/**
	 * Parse the formatted string representation of an object and return the object.
	 * @param formattedString the formatted string representation
	 * @return the parsed object
	 * @throws InvalidFormatException the formatted string was in an invalid form
	 */
	public Object parse(String formattedString) throws InvalidFormatException;

}
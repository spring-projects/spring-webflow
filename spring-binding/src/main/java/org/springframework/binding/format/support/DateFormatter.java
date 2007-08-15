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
package org.springframework.binding.format.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.springframework.binding.format.InvalidFormatException;

/**
 * Formatter that formats date objects.
 * 
 * @author Keith Donald
 */
public class DateFormatter extends AbstractFormatter {

	private DateFormat dateFormat;

	/**
	 * Constructs a date formatter that will delegate to the specified date format.
	 * @param dateFormat the date format to use
	 */
	public DateFormatter(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * Constructs a date formatter that will delegate to the specified date format.
	 * @param dateFormat the date format to use
	 * @param allowEmpty should this formatter allow empty input arguments?
	 */
	public DateFormatter(DateFormat dateFormat, boolean allowEmpty) {
		super(allowEmpty);
		this.dateFormat = dateFormat;
	}

	// convert from date to string
	protected String doFormatValue(Object date) {
		return dateFormat.format((Date) date);
	}

	// convert back from string to date
	protected Object doParseValue(String formattedString, Class targetClass) throws ParseException {
		return dateFormat.parse(formattedString);
	}

	/**
	 * Convenience method to parse a date.
	 */
	public Date parseDate(String formattedString) throws InvalidFormatException {
		return (Date) parseValue(formattedString, Date.class);
	}
}
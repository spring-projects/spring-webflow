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
package org.springframework.binding.format.factories;

import java.text.ParseException;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for all formatters.
 * 
 * @author Keith Donald
 */
public abstract class AbstractFormatter implements Formatter {

	/**
	 * Constructs a formatter.
	 */
	protected AbstractFormatter() {
	}

	public final String formatValue(Object value) {
		if (isEmpty(value)) {
			return getEmptyFormattedValue();
		} else {
			return doFormatValue(value);
		}
	}

	public final Object parseValue(String formattedString) throws InvalidFormatException {
		try {
			if (isEmpty(formattedString)) {
				return getEmptyValue();
			}
			return doParseValue(formattedString);
		} catch (ParseException ex) {
			throw new InvalidFormatException(formattedString, getExpectedFormat(), ex);
		}
	}

	/**
	 * Template method subclasses should override to encapsulate formatting logic.
	 * @param value the value to format
	 * @return the formatted string representation
	 */
	protected abstract String doFormatValue(Object value);

	/**
	 * Returns the formatted form of an empty value. Default implementation just returns the empty string.
	 */
	protected String getEmptyFormattedValue() {
		return "";
	}

	/**
	 * Template method subclasses should override to encapsulate parsing logic.
	 * @param formattedString the formatted string to parse
	 * @return the parsed value
	 * @throws InvalidFormatException an exception occurred parsing
	 * @throws ParseException when parse exceptions occur
	 */
	protected abstract Object doParseValue(String formattedString) throws InvalidFormatException, ParseException;

	/**
	 * Returns the empty value (resulting from parsing an empty input string). This default implementation just returns
	 * null.
	 */
	protected Object getEmptyValue() {
		return null;
	}

	/**
	 * Returns the expected string format for the given target class. The default implementation just returns null.
	 */
	protected String getExpectedFormat() {
		return null;
	}

	/**
	 * Is given object <i>empty</i> (null or empty string)?
	 */
	protected boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		} else if (o instanceof String) {
			return !StringUtils.hasText((String) o);
		} else {
			return false;
		}
	}
}
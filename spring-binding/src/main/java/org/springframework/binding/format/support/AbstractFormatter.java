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

import java.text.ParseException;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for all formatters.
 * 
 * @author Keith Donald
 */
public abstract class AbstractFormatter implements Formatter {

	/**
	 * Does this formatter allow empty values?
	 */
	private boolean allowEmpty = true;

	/**
	 * Constructs a formatter.
	 */
	protected AbstractFormatter() {
	}

	/**
	 * Constructs a formatter.
	 * @param allowEmpty allow formatting of empty (null or blank) values?
	 */
	protected AbstractFormatter(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}

	/**
	 * Allow formatting of empty (null or blank) values?
	 */
	public boolean isAllowEmpty() {
		return allowEmpty;
	}

	public final String formatValue(Object value) {
		if (allowEmpty && isEmpty(value)) {
			return getEmptyFormattedValue();
		}
		Assert.isTrue(!isEmpty(value), "Object to format cannot be empty");
		return doFormatValue(value);
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

	public final Object parseValue(String formattedString, Class targetClass) throws InvalidFormatException {
		try {
			if (allowEmpty && isEmpty(formattedString)) {
				return getEmptyValue();
			}
			return doParseValue(formattedString, targetClass);
		} catch (ParseException ex) {
			throw new InvalidFormatException(formattedString, getExpectedFormat(targetClass), ex);
		}
	}

	/**
	 * Template method subclasses should override to encapsulate parsing logic.
	 * @param formattedString the formatted string to parse
	 * @return the parsed value
	 * @throws InvalidFormatException an exception occured parsing
	 * @throws ParseException when parse exceptions occur
	 */
	protected abstract Object doParseValue(String formattedString, Class targetClass) throws InvalidFormatException,
			ParseException;

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
	protected String getExpectedFormat(Class targetClass) {
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
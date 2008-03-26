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
package org.springframework.binding.format.formatters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;

public class DateFormatter implements Formatter {

	private String pattern;

	private Locale locale;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String format(Object date) {
		return getDateFormat().format((Date) date);
	}

	public Object parse(String formattedString) throws InvalidFormatException {
		DateFormat dateFormat = getDateFormat();
		try {
			return dateFormat.parse(formattedString);
		} catch (ParseException e) {
			throw new InvalidFormatException(formattedString, dateFormat.toString());
		}
	}

	protected DateFormat getDateFormat() {
		if (pattern != null) {
			return new SimpleDateFormat(pattern, locale);
		} else {
			return DateFormat.getDateInstance();
		}
	}

}
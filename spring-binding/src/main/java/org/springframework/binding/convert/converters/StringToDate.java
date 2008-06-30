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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

/**
 * A formatter for {@link Date} types. Allows the configuration of an explicit date pattern and locale.
 * @see SimpleDateFormat
 * @author Keith Donald
 */
public class StringToDate extends StringToObject {

	private static Log logger = LogFactory.getLog(StringToDate.class);

	/**
	 * The default date pattern.
	 */
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

	private String pattern;

	private Locale locale;

	public StringToDate() {
		super(Date.class);
	}

	/**
	 * The pattern to use to format date values. If not specified, the default pattern 'yyyy-MM-dd' is used.
	 * @return the date formatting pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the pattern to use to format date values.
	 * @param pattern the date formatting pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * The locale to use in formatting date values. If not specified, the locale of the current thread is used.
	 * @see LocaleContextHolder#getLocale()
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale to use in formatting date values.
	 * @param locale the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Object toObject(String string, Class targetClass) throws Exception {
		if (!StringUtils.hasText(string)) {
			return null;
		}
		DateFormat dateFormat = getDateFormat();
		try {
			return dateFormat.parse(string);
		} catch (ParseException e) {
			throw new InvalidFormatException(string, getPattern(dateFormat), e);
		}
	}

	public String toString(Object target) throws Exception {
		Date date = (Date) target;
		if (date == null) {
			return "";
		}
		return getDateFormat().format(date);
	}

	// subclassing hookings

	protected DateFormat getDateFormat() {
		Locale locale = determineLocale(this.locale);
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		format.setLenient(false);
		if (format instanceof SimpleDateFormat) {
			String pattern = determinePattern(this.pattern);
			((SimpleDateFormat) format).applyPattern(pattern);
		} else {
			logger.warn("Unable to apply format pattern '" + pattern
					+ "'; Returned DateFormat is not a SimpleDateFormat");
		}
		return format;
	}

	// internal helpers

	private String determinePattern(String pattern) {
		return pattern != null ? pattern : DEFAULT_PATTERN;
	}

	private Locale determineLocale(Locale locale) {
		return locale != null ? locale : LocaleContextHolder.getLocale();
	}

	private String getPattern(DateFormat format) {
		if (format instanceof SimpleDateFormat) {
			return ((SimpleDateFormat) format).toPattern();
		} else {
			logger.warn("Pattern string cannot be determined because DateFormat is not a SimpleDateFormat");
			return "defaultDateFormatInstance";
		}
	}

}
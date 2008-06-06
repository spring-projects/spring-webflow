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
package org.springframework.binding.format.formatters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * A formatter for common number types such as integers and big decimals. Allows the configuration of an explicit number
 * pattern and locale.
 * @see DecimalFormat
 * @author Keith Donald
 */
public class NumberFormatter implements Formatter {

	private static Log logger = LogFactory.getLog(NumberFormatter.class);

	private String pattern;

	private Class numberClass;

	private Locale locale;

	/**
	 * Creates a number formatter for the specified number type.
	 * @param numberClass the number type, a class extending from {@link Number}.
	 */
	public NumberFormatter(Class numberClass) {
		Assert.notNull(numberClass, "The number class is required");
		this.numberClass = numberClass;
	}

	/**
	 * The pattern to use to format number values. If not specified, the default DecimalFormat pattern is used.
	 * @return the date formatting pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the pattern for formatting numbers.
	 * @param pattern the format pattern
	 * @see DecimalFormat
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * The locale to use in formatting number values. If null, the locale associated with the current thread is used.
	 * @see LocaleContextHolder#getLocale()
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets the locale to use in formatting number values.
	 * @param locale the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String format(Object number) {
		if (number == null) {
			return "";
		}
		return getNumberFormat().format(number);
	}

	public Object parse(String formattedString) throws InvalidFormatException {
		if (!StringUtils.hasText(formattedString)) {
			return null;
		}
		ParsePosition parsePosition = new ParsePosition(0);
		NumberFormat format = getNumberFormat();
		Number number = format.parse(formattedString, parsePosition);
		if (number == null || formattedString.length() != parsePosition.getIndex()) {
			throw new InvalidFormatException(formattedString, getPattern(format));
		} else {
			return NumberUtils.convertNumberToTargetClass(number, numberClass);
		}
	}

	// subclassing hookings

	protected NumberFormat getNumberFormat() {
		Locale locale = determineLocale(this.locale);
		NumberFormat format = NumberFormat.getInstance(locale);
		if (pattern != null) {
			if (format instanceof DecimalFormat) {
				((DecimalFormat) format).applyPattern(pattern);
			} else {
				logger.warn("Unable to apply format pattern '" + pattern
						+ "'; Returned NumberFormat is not a DecimalFormat");
			}
		}
		return format;
	}

	// internal helpers

	private Locale determineLocale(Locale locale) {
		return locale != null ? locale : LocaleContextHolder.getLocale();
	}

	private String getPattern(NumberFormat format) {
		if (format instanceof DecimalFormat) {
			return ((DecimalFormat) format).toPattern();
		} else {
			logger.warn("Pattern string cannot be determined because NumberFormat is not a DecimalFormat");
			return "defaultNumberFormatInstance";
		}
	}
}
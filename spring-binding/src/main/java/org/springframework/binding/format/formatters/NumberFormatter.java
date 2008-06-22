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
 * A general formatter for common number types such as integers and big decimals. Allows the configuration of an
 * explicit number pattern and locale.
 * 
 * Works with a general purpose {@link DecimalFormat} instance returned by calling
 * {@link NumberFormat#getInstance(Locale)} by default. This instance supports parsing any number type generally and
 * will not perform special type-specific logic such as rounding or truncation. Subclasses may override.
 * 
 * Will coerse parsed Numbers to the desired numberClass as necessary. If type-coersion results in an overflow
 * condition; for example, what can occur with a Long being coersed to a Short, an exception will be thrown.
 * 
 * @see NumberFormat
 * @see DecimalFormat
 * 
 * @author Keith Donald
 */
public class NumberFormatter implements Formatter {

	private static Log logger = LogFactory.getLog(NumberFormatter.class);

	private String pattern;

	private Class numberClass;

	private Locale locale;

	private boolean lenient;

	/**
	 * Creates a number formatter for the specified number type.
	 * @param numberClass the number type, a class extending from {@link Number}.
	 */
	public NumberFormatter(Class numberClass) {
		Assert.notNull(numberClass, "The number class is required");
		Assert.isTrue(Number.class.isAssignableFrom(numberClass), "The class must extend from Number");
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

	/**
	 * If this Formatter is "lenient" in parsing number strings. A lenient formatter does not require that all
	 * characters in the String be parsed successfully. Default is false.
	 * @return the lenient flag
	 */
	public boolean getLenient() {
		return lenient;
	}

	/**
	 * Sets if this Formatter should parse leniently.
	 * @param lenient the lenient flag
	 */
	public void setLenient(boolean lenient) {
		this.lenient = lenient;
	}

	// implementing Formatter

	public Class getObjectType() {
		return numberClass;
	}

	public String format(Object number) {
		if (number == null) {
			return "";
		}
		if (!numberClass.isInstance(number)) {
			throw new IllegalArgumentException("Object is not a [" + numberClass.getName() + "]; it is a ["
					+ number.getClass().getName() + "]");
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
		if (number == null) {
			// no object could be parsed
			throw new InvalidFormatException(formattedString, getPattern(format));
		}
		if (!lenient) {
			if (formattedString.length() != parsePosition.getIndex()) {
				// indicates a part of the string that was not parsed; e.g. ".5" in 1234.5 when parsing an Integer
				throw new InvalidFormatException(formattedString, getPattern(format));
			}
		}
		return convertToNumberClass(number);
	}

	// subclassing hookings

	/**
	 * Factory method that returns a fully-configured {@link NumberFormat} instance to use to format an object for
	 * display. Applies the locale and pattern properties if configured. Subclasses may override.
	 */
	protected NumberFormat getNumberFormat() {
		Locale locale = determineLocale(this.locale);
		NumberFormat format = createNumberFormat(locale);
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

	/**
	 * Delegates to the {@link NumberFormat java.text.NumberFormat API} to construct the new NumberFormat instance.
	 * Called by {@link #getNumberFormat()} after calculating the Locale. Subclasses may override to control how the
	 * Format instance is constructed.
	 * @param locale the calculated Locale
	 * @return the new NumberFormat instance
	 */
	protected NumberFormat createNumberFormat(Locale locale) {
		return NumberFormat.getInstance(locale);
	}

	/**
	 * Coerces the Number object returned by NumberFormat to the desired numberClass. Subclasses may override.
	 * @param number the parsed number
	 * @return the coersed number
	 * @throws IllegalArgumentException when an overflow condition occurs during coersion
	 */
	protected Number convertToNumberClass(Number number) throws IllegalArgumentException {
		return NumberUtils.convertNumberToTargetClass(number, numberClass);
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
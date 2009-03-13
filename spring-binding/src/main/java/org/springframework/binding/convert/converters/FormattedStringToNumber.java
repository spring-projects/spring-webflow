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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.format.DefaultNumberFormatFactory;
import org.springframework.binding.format.NumberFormatFactory;
import org.springframework.util.NumberUtils;

/**
 * A converter for common number types such as integers and big decimals. Allows the configuration of an explicit number
 * pattern and locale.
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
public class FormattedStringToNumber extends StringToObject {

	private static Log logger = LogFactory.getLog(FormattedStringToNumber.class);

	private NumberFormatFactory numberFormatFactory = new DefaultNumberFormatFactory();

	private boolean lenient;

	public FormattedStringToNumber() {
		super(Number.class);
	}

	public FormattedStringToNumber(Class numberClass) {
		super(numberClass);
	}

	/**
	 * Sets the factory that returns the {@link NumberFormat} instance that will format numbers handled by this
	 * converter.
	 * @param numberFormatFactory the number format factory
	 */
	public void setNumberFormatFactory(NumberFormatFactory numberFormatFactory) {
		this.numberFormatFactory = numberFormatFactory;
	}

	/**
	 * If this Converter is "lenient" in parsing number strings. A lenient converter does not require that all
	 * characters in the String be parsed successfully. Default is false.
	 * @return the lenient flag
	 */
	public boolean getLenient() {
		return lenient;
	}

	/**
	 * Sets if this Converter should parse leniently.
	 * @param lenient the lenient flag
	 */
	public void setLenient(boolean lenient) {
		this.lenient = lenient;
	}

	protected Object toObject(String string, Class targetClass) throws Exception {
		ParsePosition parsePosition = new ParsePosition(0);
		NumberFormat format = numberFormatFactory.getNumberFormat();
		Number number = format.parse(string, parsePosition);
		if (number == null) {
			// no object could be parsed
			throw new InvalidFormatException(string, getPattern(format));
		}
		if (!lenient) {
			if (string.length() != parsePosition.getIndex()) {
				// indicates a part of the string that was not parsed; e.g. ".5" in 1234.5 when parsing an Integer
				throw new InvalidFormatException(string, getPattern(format));
			}
		}
		return convertToNumberClass(number, targetClass);
	}

	protected String toString(Object object) throws Exception {
		Number number = (Number) object;
		return numberFormatFactory.getNumberFormat().format(number);
	}

	/**
	 * Coerces the Number object returned by NumberFormat to the desired numberClass. Subclasses may override.
	 * @param number the parsed number
	 * @return the coersed number
	 * @throws IllegalArgumentException when an overflow condition occurs during coersion
	 */
	protected Number convertToNumberClass(Number number, Class numberClass) throws IllegalArgumentException {
		return NumberUtils.convertNumberToTargetClass(number, numberClass);
	}

	// internal helpers

	private String getPattern(NumberFormat format) {
		if (format instanceof DecimalFormat) {
			return ((DecimalFormat) format).toPattern();
		} else {
			logger.warn("Pattern string cannot be determined because NumberFormat is not a DecimalFormat");
			return "defaultNumberFormatInstance";
		}
	}
}
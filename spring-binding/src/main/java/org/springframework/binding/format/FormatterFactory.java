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
 * Source for shared and commonly used <code>Formatters</code>.
 * <p>
 * Formatters are typically not thread safe as <code>Format</code> objects aren't thread safe: so implementations of
 * this service should take care to synchronize them as neccessary.
 * 
 * @see java.text.Format
 * 
 * @author Keith Donald
 */
public interface FormatterFactory {

	/**
	 * Returns a date formatter for the encoded date format.
	 * @param encodedFormat the format
	 * @return the formatter
	 */
	public Formatter getDateFormatter(String encodedFormat);

	/**
	 * Returns the default date format for the current locale.
	 * @return the date formatter
	 */
	public Formatter getDateFormatter();

	/**
	 * Returns the date format with the specified style for the current locale.
	 * @param style the style
	 * @return the formatter
	 */
	public Formatter getDateFormatter(Style style);

	/**
	 * Returns the default date/time format for the current locale.
	 * @return the date/time formatter
	 */
	public Formatter getDateTimeFormatter();

	/**
	 * Returns the date format with the specified styles for the current locale.
	 * @param dateStyle the date style
	 * @param timeStyle the time style
	 * @return the formatter
	 */
	public Formatter getDateTimeFormatter(Style dateStyle, Style timeStyle);

	/**
	 * Returns the default time format for the current locale.
	 * @return the time formatter
	 */
	public Formatter getTimeFormatter();

	/**
	 * Returns the time format with the specified style for the current locale.
	 * @param style the style
	 * @return the formatter
	 */
	public Formatter getTimeFormatter(Style style);

	/**
	 * Returns a number formatter for the specified class.
	 * @param numberClass the number class
	 * @return the number formatter
	 */
	public Formatter getNumberFormatter(Class numberClass);

	/**
	 * Returns a percent number formatter.
	 * @return the percent formatter
	 */
	public Formatter getPercentFormatter();

	/**
	 * Returns a currency number formatter.
	 * @return the currency formatter
	 */
	public Formatter getCurrencyFormatter();

}
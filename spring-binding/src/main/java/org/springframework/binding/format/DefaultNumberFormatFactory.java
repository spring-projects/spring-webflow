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
package org.springframework.binding.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Works with a general purpose {@link DecimalFormat} instance returned by calling
 * {@link NumberFormat#getInstance(Locale)} by default. This instance supports parsing any number type generally and
 * will not perform special type-specific logic such as rounding or truncation.
 * 
 * @see NumberFormat
 * @see DecimalFormat
 * 
 * @author Keith Donald
 */
public class DefaultNumberFormatFactory extends AbstractNumberFormatFactory {

	private static Log logger = LogFactory.getLog(DefaultNumberFormatFactory.class);

	private String pattern;

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

	protected NumberFormat getNumberFormat(Locale locale) {
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

}
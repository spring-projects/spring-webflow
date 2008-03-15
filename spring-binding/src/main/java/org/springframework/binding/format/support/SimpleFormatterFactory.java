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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.Style;

/**
 * Simple FormatterFactory implementation.
 * 
 * @author Keith Donald
 */
public class SimpleFormatterFactory extends AbstractFormatterFactory {

	public Formatter getDateFormatter(Style style) {
		return new DateFormatter(SimpleDateFormat.getDateInstance(style.shortValue(), getLocale()));
	}

	public Formatter getDateTimeFormatter(Style dateStyle, Style timeStyle) {
		return new DateFormatter(SimpleDateFormat.getDateTimeInstance(dateStyle.shortValue(), timeStyle.shortValue(),
				getLocale()));
	}

	public Formatter getTimeFormatter(Style style) {
		return new DateFormatter(SimpleDateFormat.getTimeInstance(style.shortValue(), getLocale()));
	}

	public Formatter getNumberFormatter(Class numberClass) {
		if (numberClass.equals(Integer.class) || numberClass.equals(int.class)) {
			return new NumberFormatter(NumberFormat.getIntegerInstance(getLocale()));
		} else {
			return new NumberFormatter(NumberFormat.getNumberInstance(getLocale()));
		}
	}

	public Formatter getCurrencyFormatter() {
		return new NumberFormatter(NumberFormat.getCurrencyInstance(getLocale()));
	}

	public Formatter getDateFormatter(String encodedFormat) {
		return new DateFormatter(new SimpleDateFormat(encodedFormat, getLocale()));
	}

	public Formatter getPercentFormatter() {
		return new NumberFormatter(NumberFormat.getPercentInstance(getLocale()));
	}
}
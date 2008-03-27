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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

public class NumberFormatter implements Formatter {

	private String pattern;

	private Class numberClass;

	public NumberFormatter(Class numberClass) {
		Assert.notNull(numberClass, "The number class is required");
		this.numberClass = numberClass;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String format(Object number) {
		if (number == null) {
			return "";
		}
		if (pattern != null) {
			return getNumberFormat().format(number);
		} else {
			return number.toString();
		}
	}

	public Object parse(String formattedString) throws InvalidFormatException {
		if (!StringUtils.hasText(formattedString)) {
			return null;
		}
		if (pattern != null) {
			return NumberUtils.parseNumber(formattedString, numberClass, getNumberFormat());
		} else {
			return NumberUtils.parseNumber(formattedString, numberClass);
		}
	}

	private NumberFormat getNumberFormat() {
		return new DecimalFormat(pattern);
	}
}
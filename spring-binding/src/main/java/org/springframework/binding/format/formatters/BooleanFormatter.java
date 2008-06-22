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

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A formatter for boolean values. Formats {@link Boolean#TRUE} as "true" and {@link Boolean#FALSE} as "false".
 * 
 * @author Keith Donald
 */
public class BooleanFormatter implements Formatter {

	public Class getObjectType() {
		return Boolean.class;
	}

	public String format(Object value) throws IllegalArgumentException {
		if (value == null) {
			return "";
		}
		Assert.isInstanceOf(Boolean.class, value, "Object is not a [java.lang.Boolean]");
		if (Boolean.TRUE.equals(value)) {
			return "true";
		} else {
			return "false";
		}
	}

	public Object parse(String formattedString) throws InvalidFormatException {
		if (!StringUtils.hasText(formattedString)) {
			return null;
		}
		if (formattedString.equals("true")) {
			return Boolean.TRUE;
		} else if (formattedString.equals("false")) {
			return Boolean.FALSE;
		} else {
			throw new InvalidFormatException(formattedString, "true | false");
		}
	}
}
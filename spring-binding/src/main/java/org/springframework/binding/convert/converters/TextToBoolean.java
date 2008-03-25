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
package org.springframework.binding.convert.converters;

import org.springframework.util.StringUtils;

/**
 * Converts a textual representation of a boolean object to a <code>Boolean</code> instance.
 * 
 * @author Keith Donald
 */
public class TextToBoolean extends AbstractConverter {

	private static final String VALUE_TRUE = "true";

	private static final String VALUE_FALSE = "false";

	private static final String VALUE_ON = "on";

	private static final String VALUE_OFF = "off";

	private static final String VALUE_YES = "yes";

	private static final String VALUE_NO = "no";

	private static final String VALUE_1 = "1";

	private static final String VALUE_0 = "0";

	private String trueString;

	private String falseString;

	/**
	 * Default constructor. No special true or false strings are considered.
	 */
	public TextToBoolean() {
		this(null, null);
	}

	/**
	 * Create a text to boolean converter. Take given <i>special</i> string representations of true and false into
	 * account.
	 * @param trueString special true string to consider
	 * @param falseString special false string to consider
	 */
	public TextToBoolean(String trueString, String falseString) {
		this.trueString = trueString;
		this.falseString = falseString;
	}

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { Boolean.class };
	}

	protected Object doConvert(Object source, Class targetClass, Object context) throws Exception {
		String text = (String) source;
		if (!StringUtils.hasText(text)) {
			return null;
		} else if (this.trueString != null && text.equalsIgnoreCase(this.trueString)) {
			return Boolean.TRUE;
		} else if (this.falseString != null && text.equalsIgnoreCase(this.falseString)) {
			return Boolean.FALSE;
		} else if (this.trueString == null
				&& (text.equalsIgnoreCase(VALUE_TRUE) || text.equalsIgnoreCase(VALUE_ON)
						|| text.equalsIgnoreCase(VALUE_YES) || text.equals(VALUE_1))) {
			return Boolean.TRUE;
		} else if (this.falseString == null
				&& (text.equalsIgnoreCase(VALUE_FALSE) || text.equalsIgnoreCase(VALUE_OFF)
						|| text.equalsIgnoreCase(VALUE_NO) || text.equals(VALUE_0))) {
			return Boolean.FALSE;
		} else {
			throw new IllegalArgumentException("Invalid boolean value [" + text + "]");
		}
	}
}
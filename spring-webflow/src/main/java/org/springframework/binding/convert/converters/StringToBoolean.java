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

/**
 * Converts a textual representation of a boolean object to a <code>Boolean</code> instance.
 * 
 * @author Keith Donald
 */
public class StringToBoolean extends StringToObject {

	private static final String VALUE_TRUE = "true";

	private static final String VALUE_FALSE = "false";

	private String trueString;

	private String falseString;

	/**
	 * Create a text boolean converter that parses standard true and false strings.
	 */
	public StringToBoolean() {
		super(Boolean.class);
	}

	/**
	 * Create a text to boolean converter that takes specific string representations of true and false into account.
	 * @param trueString special true string to use
	 * @param falseString special false string to use
	 */
	public StringToBoolean(String trueString, String falseString) {
		super(Boolean.class);
		this.trueString = trueString;
		this.falseString = falseString;
	}

	protected Object toObject(String string, Class targetClass) throws Exception {
		if (trueString != null && string.equals(trueString)) {
			return Boolean.TRUE;
		} else if (falseString != null && string.equals(falseString)) {
			return Boolean.FALSE;
		} else if (trueString == null && string.equals(VALUE_TRUE)) {
			return Boolean.TRUE;
		} else if (falseString == null && string.equals(VALUE_FALSE)) {
			return Boolean.FALSE;
		} else {
			throw new IllegalArgumentException("Invalid boolean value [" + string + "]");
		}
	}

	protected String toString(Object object) throws Exception {
		Boolean value = (Boolean) object;
		if (Boolean.TRUE.equals(value)) {
			if (trueString != null) {
				return trueString;
			} else {
				return VALUE_TRUE;
			}
		} else if (Boolean.FALSE.equals(value)) {
			if (falseString != null) {
				return falseString;
			} else {
				return VALUE_FALSE;
			}
		} else {
			throw new IllegalArgumentException("Invalid boolean value [" + value + "]");
		}
	}

}
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

import org.springframework.binding.convert.Converter;
import org.springframework.binding.format.Formatter;

/**
 * Adapts a Formatter to the Converter interface. Allows a Formatter to be used as a Converter. Allows for both the
 * to-string and from-string logic to be used.
 * 
 * @see Formatter
 * @see Formatter#format(Object)
 * @see Formatter#parse(String)
 * 
 * @author Keith Donald
 */
public class FormatterConverter implements Converter {

	private Formatter formatter;

	/**
	 * Creates a new formatter converter.
	 * @param formatter the formatter instance to adapt to the Converter API
	 */
	public FormatterConverter(Formatter formatter) {
		this.formatter = formatter;
	}

	public Class[] getSourceClasses() {
		return new Class[] { formatter.getObjectType(), String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { String.class, formatter.getObjectType() };
	}

	public Object convert(Object source, Class targetClass, Object context) throws Exception {
		if (targetClass.equals(String.class)) {
			return formatter.format(source);
		} else {
			return formatter.parse((String) source);
		}
	}

}
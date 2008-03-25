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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.binding.format.FormatterRegistry;

/**
 * Converts textual representations of numbers to a <code>Number</code> specialization. Delegates to a synchronized
 * formatter to parse text strings.
 * 
 * @author Keith Donald
 */
public class TextToNumber extends AbstractFormattingConverter {

	/**
	 * Create a string to number converter using given formatter factory.
	 * @param formatterRegistry the formatter registry to use
	 */
	public TextToNumber(FormatterRegistry formatterRegistry) {
		super(formatterRegistry);
	}

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { Integer.class, Short.class, Long.class, Float.class, Double.class, Byte.class,
				BigInteger.class, BigDecimal.class };
	}
}
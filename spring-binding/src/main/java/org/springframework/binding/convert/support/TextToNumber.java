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
package org.springframework.binding.convert.support;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.format.FormatterFactory;
import org.springframework.binding.format.support.SimpleFormatterFactory;

/**
 * Converts textual representations of numbers to a <code>Number</code> specialization. Delegates to a synchronized
 * formatter to parse text strings.
 * 
 * @author Keith Donald
 */
public class TextToNumber extends AbstractFormattingConverter {

	/**
	 * Default constructor that uses a {@link SimpleFormatterFactory}.
	 */
	public TextToNumber() {
		super(new SimpleFormatterFactory());
	}

	/**
	 * Create a string to number converter using given formatter factory.
	 * @param formatterFactory the factory to use
	 */
	public TextToNumber(FormatterFactory formatterFactory) {
		super(formatterFactory);
	}

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { Integer.class, Short.class, Byte.class, Long.class, Float.class, Double.class,
				BigInteger.class, BigDecimal.class };
	}

	protected Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception {
		return getFormatterFactory().getNumberFormatter(targetClass).parseValue((String) source, targetClass);
	}
}
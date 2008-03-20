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

import org.springframework.binding.format.FormatterRegistry;
import org.springframework.util.Assert;

/**
 * A converter that delegates to a formatter to perform the conversion. Formatters are typically not thread safe, so we
 * use a FormatterRegistry that is expected to provide us with instances local to the current thread.
 * 
 * @author Keith Donald
 */
public abstract class AbstractFormattingConverter extends AbstractConverter {

	/**
	 * The formatter registry.
	 */
	private FormatterRegistry formatterRegistry;

	/**
	 * Creates a new converter that delegates to a formatter.
	 * @param formatterRegistry the formatterRegistry to use
	 */
	protected AbstractFormattingConverter(FormatterRegistry formatterRegistry) {
		Assert.notNull(formatterRegistry, "The formatter registry is required");
		this.formatterRegistry = formatterRegistry;
	}

	public FormatterRegistry getFormatterRegistry() {
		return formatterRegistry;
	}

	protected Object doConvert(Object source, Class targetClass, Object context) throws Exception {
		return getFormatterRegistry().getFormatter(targetClass).parseValue((String) source);
	}
}
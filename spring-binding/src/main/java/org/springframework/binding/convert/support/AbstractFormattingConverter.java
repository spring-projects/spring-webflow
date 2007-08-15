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

import org.springframework.binding.format.FormatterFactory;

/**
 * A converter that delegates to a formatter to perform the conversion. Formatters are typically not thread safe, so we
 * use a FormatterFactory that is expected to provide us with thread-safe instances as necessary.
 * 
 * @author Keith Donald
 */
public abstract class AbstractFormattingConverter extends AbstractConverter {

	/**
	 * The formatter factory.
	 */
	private FormatterFactory formatterFactory;

	/**
	 * Creates a new converter that delegates to a formatter.
	 * @param formatterFactory the factory to use
	 */
	protected AbstractFormattingConverter(FormatterFactory formatterFactory) {
		setFormatterFactory(formatterFactory);
	}

	protected FormatterFactory getFormatterFactory() {
		return formatterFactory;
	}

	public void setFormatterFactory(FormatterFactory formatterSource) {
		this.formatterFactory = formatterSource;
	}
}
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
import org.springframework.binding.format.factories.DateFormatterFactory;
import org.springframework.binding.format.factories.NumberFormatterFactory;
import org.springframework.binding.format.impl.FormatterRegistryImpl;

/**
 * Default, local implementation of a conversion service. Will automatically register <i>from string</i> converters for
 * a number of standard Java types like Class, Number, Boolean and so on.
 * 
 * @author Keith Donald
 */
public class DefaultConversionService extends GenericConversionService {

	/**
	 * Returns the formatter registry used by this conversion service
	 */
	private FormatterRegistry formatterRegistry;

	/**
	 * Creates a new default conversion service, installing the default converters.
	 */
	public DefaultConversionService() {
		formatterRegistry = createDefaultFormatterRegistry();
		addDefaultConverters();
	}

	/**
	 * Add all default converters to the conversion service.
	 */
	protected void addDefaultConverters() {
		addConverter(new TextToClass());
		addConverter(new TextToBoolean());
		addConverter(new TextToLabeledEnum());
		addConverter(new TextToNumber(formatterRegistry));
		addConverter(new TextToDate(formatterRegistry));
	}

	protected FormatterRegistry createDefaultFormatterRegistry() {
		FormatterRegistryImpl registry = new FormatterRegistryImpl();
		registry.registerFormatter(new NumberFormatterFactory());
		registry.registerFormatter(new DateFormatterFactory());
		return registry;
	}
}
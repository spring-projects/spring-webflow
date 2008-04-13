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
package org.springframework.binding.format.registry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.binding.format.formatters.BooleanFormatter;
import org.springframework.binding.format.formatters.DateFormatter;
import org.springframework.binding.format.formatters.NumberFormatter;

public class DefaultFormatterRegistry extends GenericFormatterRegistry {

	/**
	 * A singleton shared instance. Should never be modified.
	 */
	private static DefaultFormatterRegistry SHARED_INSTANCE;

	/**
	 * Creates a new formatter registry.
	 */
	public DefaultFormatterRegistry() {
		registerDefaultFormatters();
	}

	/**
	 * Registers the default formatters. Subclasses may override.
	 */
	protected void registerDefaultFormatters() {
		registerFormatter(Integer.class, new NumberFormatter(Integer.class));
		registerFormatter(Long.class, new NumberFormatter(Long.class));
		registerFormatter(Short.class, new NumberFormatter(Short.class));
		registerFormatter(Float.class, new NumberFormatter(Float.class));
		registerFormatter(Double.class, new NumberFormatter(Double.class));
		registerFormatter(Byte.class, new NumberFormatter(Byte.class));
		registerFormatter(BigInteger.class, new NumberFormatter(BigInteger.class));
		registerFormatter(BigDecimal.class, new NumberFormatter(BigDecimal.class));
		registerFormatter(Boolean.class, new BooleanFormatter());
		registerFormatter(Date.class, new DateFormatter());
	}

	/**
	 * Returns the shared {@link DefaultConversionService} instance.
	 */
	public synchronized static FormatterRegistry getSharedInstance() {
		if (SHARED_INSTANCE == null) {
			SHARED_INSTANCE = new DefaultFormatterRegistry();
		}
		return SHARED_INSTANCE;
	}
}

/*
 * Copyright 2004-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.convert.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import org.springframework.core.convert.ConversionService;

/**
 * Default, local implementation of a conversion service. Will automatically register <i>from string</i> converters for
 * a number of standard Java types like Class, Number, Boolean and so on.
 * 
 * @author Keith Donald
 */
public class DefaultConversionService extends GenericConversionService {

	/**
	 * Creates a new default conversion service, installing the default converters.
	 */
	public DefaultConversionService() {
		addDefaultConverters();
		addDefaultAliases();
	}

	/**
	 * Creates a new default conversion service with an instance of a Spring ConversionService.
	 * 
	 * @param delegateConversionService the Spring conversion service
	 */
	public DefaultConversionService(ConversionService delegateConversionService) {
		super(delegateConversionService);
		addDefaultConverters();
		addDefaultAliases();
	}

	/**
	 * Add all default converters to the conversion service.
	 * 
	 * Note: Staring with Spring Web Flow 2.1, this method does not register any Spring Binding converters. All type
	 * conversion is driven through Spring's type conversion instead.
	 * 
	 * @see GenericConversionService
	 */
	protected void addDefaultConverters() {
	}

	protected void addDefaultAliases() {
		addAlias("string", String.class);
		addAlias("byte", Byte.class);
		addAlias("boolean", Boolean.class);
		addAlias("character", Character.class);
		addAlias("short", Short.class);
		addAlias("integer", Integer.class);
		addAlias("long", Long.class);
		addAlias("float", Float.class);
		addAlias("double", Double.class);
		addAlias("bigInteger", BigInteger.class);
		addAlias("bigDecimal", BigDecimal.class);
		addAlias("locale", Locale.class);
		addAlias("date", Date.class);
	}

}
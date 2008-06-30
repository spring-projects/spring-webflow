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
package org.springframework.binding.convert.service;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.converters.StringToBigDecimal;
import org.springframework.binding.convert.converters.StringToBigInteger;
import org.springframework.binding.convert.converters.StringToBoolean;
import org.springframework.binding.convert.converters.StringToByte;
import org.springframework.binding.convert.converters.StringToCharacter;
import org.springframework.binding.convert.converters.StringToClass;
import org.springframework.binding.convert.converters.StringToDate;
import org.springframework.binding.convert.converters.StringToDouble;
import org.springframework.binding.convert.converters.StringToFloat;
import org.springframework.binding.convert.converters.StringToInteger;
import org.springframework.binding.convert.converters.StringToLabeledEnum;
import org.springframework.binding.convert.converters.StringToLong;
import org.springframework.binding.convert.converters.StringToShort;

/**
 * Default, local implementation of a conversion service. Will automatically register <i>from string</i> converters for
 * a number of standard Java types like Class, Number, Boolean and so on.
 * 
 * @author Keith Donald
 */
public class DefaultConversionService extends GenericConversionService {

	/**
	 * A singleton shared instance. Should never be modified.
	 */
	private static DefaultConversionService SHARED_INSTANCE;

	/**
	 * Creates a new default conversion service, installing the default converters.
	 */
	public DefaultConversionService() {
		addDefaultConverters();
	}

	/**
	 * Add all default converters to the conversion service.
	 */
	protected void addDefaultConverters() {
		addConverter(new StringToByte());
		addConverter(new StringToBoolean());
		addConverter(new StringToCharacter());
		addConverter(new StringToShort());
		addConverter(new StringToInteger());
		addConverter(new StringToLong());
		addConverter(new StringToFloat());
		addConverter(new StringToDouble());
		addConverter(new StringToBigInteger());
		addConverter(new StringToBigDecimal());
		addConverter(new StringToClass());
		addConverter(new StringToLabeledEnum());
		addConverter(new StringToDate());
	}

	/**
	 * Returns the shared {@link DefaultConversionService} instance.
	 */
	public synchronized static ConversionService getSharedInstance() {
		if (SHARED_INSTANCE == null) {
			SHARED_INSTANCE = new DefaultConversionService();
		}
		return SHARED_INSTANCE;
	}
}
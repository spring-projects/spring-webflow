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

import org.springframework.binding.format.support.SimpleFormatterFactory;
import org.springframework.core.enums.LabeledEnum;

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
	}

	/**
	 * Add all default converters to the conversion service.
	 */
	protected void addDefaultConverters() {
		addConverter(new TextToClass());
		addConverter(new TextToNumber(new SimpleFormatterFactory()));
		addConverter(new TextToBoolean());
		addConverter(new TextToLabeledEnum());

		// we're not using addDefaultAlias here for efficiency reasons
		addAlias("string", String.class);
		addAlias("short", Short.class);
		addAlias("integer", Integer.class);
		addAlias("int", Integer.class);
		addAlias("byte", Byte.class);
		addAlias("long", Long.class);
		addAlias("float", Float.class);
		addAlias("double", Double.class);
		addAlias("bigInteger", BigInteger.class);
		addAlias("bigDecimal", BigDecimal.class);
		addAlias("boolean", Boolean.class);
		addAlias("class", Class.class);
		addAlias("labeledEnum", LabeledEnum.class);
	}
}
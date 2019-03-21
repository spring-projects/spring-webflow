/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.binding.convert.converters;

import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;

/**
 * A Spring Binding Converter that delegates to a Spring {@link ConversionService} to do the actual type conversion.
 * 
 * @author Rossen Stoyanchev
 */
public class SpringConvertingConverterAdapter implements Converter {

	/**
	 * The source value type to convert from.
	 */
	private final Class<?> sourceClass;

	/**
	 * The target value type to convert to.
	 */
	private final Class<?> targetClass;

	/**
	 * The ConversionService that will perform the conversion.
	 */
	private ConversionService conversionService;

	public SpringConvertingConverterAdapter(Class<?> sourceClass, Class<?> targetClass,
			ConversionService conversionService) {
		Assert.notNull(sourceClass, "The source class to convert from is required.");
		Assert.notNull(targetClass, "The target class to convert to is required.");
		Assert.notNull(conversionService, "A Spring ConversionService is required.");
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.conversionService = conversionService;
	}

	public Object convertSourceToTargetClass(Object source, Class<?> targetClass) {
		return conversionService.convert(source, targetClass);
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

}

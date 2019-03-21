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
package org.springframework.binding.convert.service;

import java.util.Collections;
import java.util.Set;

import org.springframework.binding.convert.converters.Converter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.Assert;

/**
 * A Spring Converter that makes it possible for a Spring Binding Converter to be registered with a Spring
 * {@link ConversionService}.
 * 
 * @author Rossen Stoyanchev
 */
public class SpringBindingConverterAdapter implements GenericConverter {

	private Converter converter;

	public SpringBindingConverterAdapter(Converter converter) {
		Assert.notNull(converter, "A Spring Binding converter is required.");
		this.converter = converter;
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
			return converter.convertSourceToTargetClass(source, targetType.getObjectType());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(converter.getSourceClass(), converter.getTargetClass()));
	}

}

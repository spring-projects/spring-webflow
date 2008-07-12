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
package org.springframework.binding.convert.converters;

import java.lang.reflect.Array;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;

/**
 * Special two-way converter that converts an object to an single-element array. Supports type conversion of the
 * individual array elements; for example, the ability to convert a String to an Integer[]. Mainly used internally by
 * {@link ConversionService} implementations.
 * 
 * @author Keith Donald
 */
public class ObjectToArray implements TwoWayConverter {

	private ConversionService conversionService;

	public ObjectToArray(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Class getSourceClass() {
		return Object.class;
	}

	public Class getTargetClass() {
		return Object[].class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		if (source == null) {
			return null;
		}
		if (source instanceof String) {
			String string = (String) source;
			String[] elements = string.split(",");
			Class componentType = targetClass.getComponentType();
			Object array = Array.newInstance(componentType, elements.length);
			ConversionExecutor converter = conversionService.getConversionExecutor(String.class, componentType);
			for (int i = 0; i < elements.length; i++) {
				String element = elements[i].trim();
				Array.set(array, i, converter.execute(element));
			}
			return array;
		} else {
			Class componentType = targetClass.getComponentType();
			Object array = Array.newInstance(componentType, 1);
			ConversionExecutor converter = conversionService.getConversionExecutor(source.getClass(), componentType);
			Array.set(array, 0, converter.execute(source));
			return array;
		}
	}

	public Object convertTargetToSourceClass(Object target, Class sourceClass) throws Exception {
		if (target == null) {
			return null;
		}
		if (String.class.equals(sourceClass)) {
			int length = Array.getLength(target);
			Class componentType = target.getClass().getComponentType();
			ConversionExecutor converter = conversionService.getConversionExecutor(componentType, String.class);
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < length; i++) {
				Object value = Array.get(target, i);
				if (value != null) {
					buffer.append(converter.execute(value));
				}
				if (i < length - 1) {
					buffer.append(",");
				}
			}
			return buffer.toString();
		} else {
			Object value = Array.get(target, 0);
			Class componentType = target.getClass().getComponentType();
			ConversionExecutor converter = conversionService.getConversionExecutor(componentType, sourceClass);
			return converter.execute(value);
		}
	}
}

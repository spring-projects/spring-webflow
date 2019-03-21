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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.core.ResolvableType;

/**
 * Special converter that converts from a source array to a target collection. Supports the selection of an
 * "approximate" collection implementation when a target collection interface such as <code>List.class</code> is
 * specified. Supports type conversion of array elements when a concrete parameterized collection class is provided,
 * such as {@code IntegerList<Integer>.class}.
 * 
 * Note that type erasure prevents arbitrary access to generic collection element type information at runtime,
 * preventing the ability to convert elements for collections declared as properties.
 * 
 * Mainly used internally by {@link ConversionService} implementations.
 * 
 * @author Keith Donald
 */
public class ArrayToCollection implements TwoWayConverter {

	private ConversionService conversionService;

	private ConversionExecutor elementConverter;

	/**
	 * Creates a new array to collection converter.
	 * @param conversionService the conversion service to use to lookup the converter to apply to array elements added
	 * to the target collection
	 */
	public ArrayToCollection(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Creates a new array to collection converter.
	 * @param elementConverter A specific converter to use on array elements when adding them to the target collection
	 */
	public ArrayToCollection(ConversionExecutor elementConverter) {
		this.elementConverter = elementConverter;
	}

	public Class<?> getSourceClass() {
		return Object[].class;
	}

	public Class<?> getTargetClass() {
		return Collection.class;
	}

	@SuppressWarnings("unchecked")
	public Object convertSourceToTargetClass(Object source, Class<?> targetClass) throws Exception {
		if (source == null) {
			return null;
		}
		Class<?> collectionImplClass = getCollectionImplClass(targetClass);
		Constructor<?> constructor = collectionImplClass.getConstructor();
		Collection<Object> collection = (Collection<Object>) constructor.newInstance();
		ConversionExecutor converter = getArrayElementConverter(source, targetClass);
		int length = Array.getLength(source);
		for (int i = 0; i < length; i++) {
			Object value = Array.get(source, i);
			if (converter != null) {
				value = converter.execute(value);
			}
			collection.add(value);
		}
		return collection;
	}

	public Object convertTargetToSourceClass(Object target, Class<?> sourceClass) {
		if (target == null) {
			return null;
		}
		Collection<?> collection = (Collection<?>) target;
		Object array = Array.newInstance(sourceClass.getComponentType(), collection.size());
		int i = 0;
		for (Object value : collection) {
			if (value != null) {
				ConversionExecutor converter;
				if (elementConverter != null) {
					converter = elementConverter;
				} else {
					converter = conversionService.getConversionExecutor(value.getClass(),
							sourceClass.getComponentType());
				}
				value = converter.execute(value);
			}
			Array.set(array, i++, value);
		}
		return array;
	}

	private Class<?> getCollectionImplClass(Class<?> targetClass) {
		if (targetClass.isInterface()) {
			if (List.class.equals(targetClass)) {
				return ArrayList.class;
			} else if (Set.class.equals(targetClass)) {
				return LinkedHashSet.class;
			} else if (SortedSet.class.equals(targetClass)) {
				return TreeSet.class;
			} else {
				throw new IllegalArgumentException("Unsupported collection interface [" + targetClass.getName() + "]");
			}
		} else {
			return targetClass;
		}
	}

	@SuppressWarnings("unchecked")
	private ConversionExecutor getArrayElementConverter(Object source, Class<?> targetClass) {
		if (elementConverter != null) {
			return elementConverter;
		} else {
			Class<?> elementType = ResolvableType.forClass(targetClass).asCollection().resolveGeneric(0);
			if (elementType != null) {
				Class<?> componentType = source.getClass().getComponentType();
				return conversionService.getConversionExecutor(componentType, elementType);
			}
			return null;
		}
	}

}

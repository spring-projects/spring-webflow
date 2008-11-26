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
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.JdkVersion;

/**
 * Special two-way converter that converts an object to an single-element collection. Supports type conversion of the
 * individual element with parameterized collection implementations.
 * 
 * @author Keith Donald
 */
public class ObjectToCollection implements Converter {

	private ConversionService conversionService;

	public ObjectToCollection(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Class getSourceClass() {
		return Object.class;
	}

	public Class getTargetClass() {
		return Collection.class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		if (source == null) {
			return null;
		}
		Class collectionImplClass = getCollectionImplClass(targetClass);
		Constructor constructor = collectionImplClass.getConstructor(null);
		Collection collection = (Collection) constructor.newInstance(null);
		ConversionExecutor converter = getElementConverter(source, targetClass);
		Object value;
		if (converter != null) {
			value = converter.execute(source);
		} else {
			value = source;
		}
		collection.add(value);
		return collection;
	}

	private Class getCollectionImplClass(Class targetClass) {
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

	private ConversionExecutor getElementConverter(Object source, Class targetClass) {
		if (JdkVersion.isAtLeastJava15()) {
			Class elementType = GenericCollectionTypeResolver.getCollectionType(targetClass);
			if (elementType != null) {
				Class componentType = source.getClass().getComponentType();
				return conversionService.getConversionExecutor(componentType, elementType);
			}
		}
		return null;
	}
}
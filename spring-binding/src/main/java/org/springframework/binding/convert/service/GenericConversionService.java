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

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.converters.ArrayToArray;
import org.springframework.binding.convert.converters.ArrayToCollection;
import org.springframework.binding.convert.converters.Converter;
import org.springframework.binding.convert.converters.ObjectToArray;
import org.springframework.binding.convert.converters.ReverseConverter;
import org.springframework.binding.convert.converters.TwoWayConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Base implementation of a conversion service. Initially empty, e.g. no converters are registered by default.
 * 
 * @author Keith Donald
 */
public class GenericConversionService implements ConversionService {

	/**
	 * An indexed map of converters. Each entry key is a source class that can be converted from, and each entry value
	 * is a map of target classes that can be converted to, ultimately mapping to a specific converter that can perform
	 * the source->target conversion.
	 */
	private final Map sourceClassConverters = new HashMap();

	/**
	 * Indexes classes by well-known aliases.
	 */
	private final Map aliasMap = new HashMap();

	/**
	 * An optional parent conversion service.
	 */
	private ConversionService parent;

	/**
	 * Returns the parent of this conversion service. Could be null.
	 */
	public ConversionService getParent() {
		return parent;
	}

	/**
	 * Set the parent of this conversion service. This is optional.
	 */
	public void setParent(ConversionService parent) {
		this.parent = parent;
	}

	/**
	 * Add given converter to this conversion service.
	 * @param converter the converter
	 */
	public void addConverter(Converter converter) {
		Class sourceClass = converter.getSourceClass();
		Class targetClass = converter.getTargetClass();
		if (sourceClass.isPrimitive()) {
			throw new IllegalArgumentException("Invalid Converter " + converter
					+ "; A primitive type is not allowed for the [sourceClass] argument");
		}
		if (targetClass.isPrimitive()) {
			throw new IllegalArgumentException("Invalid Converter " + converter
					+ "; A primitive type is not allowed for the [targetClass] argument");
		}
		Map sourceMap = getSourceMap(sourceClass);
		sourceMap.put(targetClass, converter);
		if (converter instanceof TwoWayConverter) {
			sourceMap = getSourceMap(targetClass);
			sourceMap.put(sourceClass, new ReverseConverter((TwoWayConverter) converter));
		}
	}

	/**
	 * Add an alias for given target type.
	 */
	public void addAlias(String alias, Class targetType) {
		aliasMap.put(alias, targetType);
	}

	private Map getSourceMap(Class sourceClass) {
		Map sourceMap = (Map) sourceClassConverters.get(sourceClass);
		if (sourceMap == null) {
			sourceMap = new HashMap();
			sourceClassConverters.put(sourceClass, sourceMap);
		}
		return sourceMap;
	}

	public ConversionExecutor getConversionExecutor(Class sourceClass, Class targetClass)
			throws ConversionExecutorNotFoundException {
		Assert.notNull(sourceClass, "The source class to convert from is required");
		Assert.notNull(targetClass, "The target class to convert to is required");
		sourceClass = convertToWrapperClassIfNecessary(sourceClass);
		targetClass = convertToWrapperClassIfNecessary(targetClass);
		if (targetClass.isAssignableFrom(sourceClass)) {
			return new StaticConversionExecutor(sourceClass, targetClass, new NoOpConverter(sourceClass, targetClass));
		}
		if (sourceClass.isArray()) {
			if (targetClass.isArray()) {
				return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToArray(this));
			} else if (Collection.class.isAssignableFrom(targetClass)) {
				if (!targetClass.isInterface() && Modifier.isAbstract(targetClass.getModifiers())) {
					throw new IllegalArgumentException("Conversion target class [" + targetClass.getName()
							+ "] is invalid; cannot convert to abstract collection types--"
							+ "request an interface or concrete implementation instead");
				}
				return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToCollection(this));
			} else {
				Converter arrayToObject = new ReverseConverter(new ObjectToArray(this));
				return new StaticConversionExecutor(sourceClass, targetClass, arrayToObject);
			}
		}
		if (targetClass.isArray()) {
			if (Collection.class.isAssignableFrom(sourceClass)) {
				Converter collectionToArray = new ReverseConverter(new ArrayToCollection(this));
				return new StaticConversionExecutor(sourceClass, targetClass, collectionToArray);
			} else {
				return new StaticConversionExecutor(sourceClass, targetClass, new ObjectToArray(this));
			}
		}
		Map sourceTargetConverters = findConvertersForSource(sourceClass);
		Converter converter = findTargetConverter(sourceTargetConverters, targetClass);
		if (converter != null) {
			// we found a converter
			return new StaticConversionExecutor(sourceClass, targetClass, converter);
		} else {
			if (parent != null) {
				// try the parent
				return parent.getConversionExecutor(sourceClass, targetClass);
			} else {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"No ConversionExecutor found for converting from sourceClass '" + sourceClass.getName()
								+ "' to target class '" + targetClass.getName() + "'");
			}
		}
	}

	public Class getClassByName(String name) throws IllegalArgumentException {
		Class clazz = (Class) aliasMap.get(name);
		if (clazz != null) {
			return clazz;
		} else {
			if (parent != null) {
				return parent.getClassByName(name);
			} else {
				try {
					return ClassUtils.forName(name);
				} catch (ClassNotFoundException e) {
					IllegalArgumentException iae = new IllegalArgumentException(
							"No Class alias or instance found with name '" + name + "' in this ConversionService");
					iae.initCause(e);
					throw iae;
				}
			}
		}
	}

	// subclassing support

	/**
	 * Returns an indexed map of converters. Each entry key is a source class that can be converted from, and each entry
	 * value is a map of target classes that can be convertered to, ultimately mapping to a specific converter that can
	 * perform the source->target conversion.
	 */
	protected Map getSourceClassConverters() {
		return sourceClassConverters;
	}

	/**
	 * Returns a registered converter object
	 * @param sourceClass the source class
	 * @param targetClass the target class
	 */
	protected Converter getConverter(Class sourceClass, Class targetClass) {
		Map sourceTargetConverters = findConvertersForSource(sourceClass);
		return findTargetConverter(sourceTargetConverters, targetClass);
	}

	// internal helpers

	private Map findConvertersForSource(Class sourceClass) {
		if (sourceClass.isInterface()) {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(sourceClass);
			while (!classQueue.isEmpty()) {
				sourceClass = (Class) classQueue.removeLast();
				Map sourceTargetConverters = (Map) sourceClassConverters.get(sourceClass);
				if (sourceTargetConverters != null && !sourceTargetConverters.isEmpty()) {
					return sourceTargetConverters;
				}
				// queue up source class's implemented interfaces.
				Class[] interfaces = sourceClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					classQueue.addFirst(interfaces[i]);
				}
			}
			Map sourceTargetConverters = (Map) sourceClassConverters.get(Object.class);
			if (sourceTargetConverters != null && !sourceTargetConverters.isEmpty()) {
				return sourceTargetConverters;
			} else {
				return Collections.EMPTY_MAP;
			}
		} else {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(sourceClass);
			while (!classQueue.isEmpty()) {
				sourceClass = (Class) classQueue.removeLast();
				Map sourceTargetConverters = (Map) sourceClassConverters.get(sourceClass);
				if (sourceTargetConverters != null && !sourceTargetConverters.isEmpty()) {
					return sourceTargetConverters;
				}
				if (sourceClass.getSuperclass() != null) {
					classQueue.addFirst(sourceClass.getSuperclass());
				}
				// queue up source class's implemented interfaces.
				Class[] interfaces = sourceClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					classQueue.addFirst(interfaces[i]);
				}
			}
			return Collections.EMPTY_MAP;
		}
	}

	private Converter findTargetConverter(Map sourceTargetConverters, Class targetClass) {
		if (targetClass.isInterface()) {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(targetClass);
			while (!classQueue.isEmpty()) {
				targetClass = (Class) classQueue.removeLast();
				Converter converter = (Converter) sourceTargetConverters.get(targetClass);
				if (converter != null) {
					return converter;
				}
				// queue up target class's implemented interfaces.
				Class[] interfaces = targetClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					classQueue.addFirst(interfaces[i]);
				}
			}
			return (Converter) sourceTargetConverters.get(Object.class);
		} else {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(targetClass);
			while (!classQueue.isEmpty()) {
				targetClass = (Class) classQueue.removeLast();
				Converter converter = (Converter) sourceTargetConverters.get(targetClass);
				if (converter != null) {
					return converter;
				}
				if (targetClass.getSuperclass() != null) {
					classQueue.addFirst(targetClass.getSuperclass());
				}
				// queue up target class's implemented interfaces.
				Class[] interfaces = targetClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					classQueue.addFirst(interfaces[i]);
				}
			}
			return null;
		}
	}

	private Class convertToWrapperClassIfNecessary(Class targetType) {
		if (targetType.isPrimitive()) {
			if (targetType.equals(int.class)) {
				return Integer.class;
			} else if (targetType.equals(short.class)) {
				return Short.class;
			} else if (targetType.equals(long.class)) {
				return Long.class;
			} else if (targetType.equals(float.class)) {
				return Float.class;
			} else if (targetType.equals(double.class)) {
				return Double.class;
			} else if (targetType.equals(byte.class)) {
				return Byte.class;
			} else if (targetType.equals(boolean.class)) {
				return Boolean.class;
			} else if (targetType.equals(char.class)) {
				return Character.class;
			} else {
				throw new IllegalStateException("Should never happen - primitive type is not a primitive?");
			}
		} else {
			return targetType;
		}
	}

}
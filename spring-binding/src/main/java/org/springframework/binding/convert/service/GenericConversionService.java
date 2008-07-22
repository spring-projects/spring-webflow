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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.springframework.binding.convert.ConversionException;
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
	 * A map of custom converters. Custom converters are assigned a unique identifier that can be used to lookup the
	 * converter. This allows multiple converters for the same source->target class to be registered.
	 */
	private final Map customConverters = new HashMap();

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
		Map sourceMap = getSourceMap(sourceClass);
		sourceMap.put(targetClass, converter);
		if (converter instanceof TwoWayConverter) {
			sourceMap = getSourceMap(targetClass);
			sourceMap.put(sourceClass, new ReverseConverter((TwoWayConverter) converter));
		}
	}

	/**
	 * Add given custom converter to this conversion service.
	 * @param id the id of the custom converter instance
	 * @param converter the converter
	 */
	public void addConverter(String id, Converter converter) {
		customConverters.put(id, converter);
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
		Converter converter = findRegisteredConverter(sourceClass, targetClass);
		if (converter != null) {
			// we found a converter
			return new StaticConversionExecutor(sourceClass, targetClass, converter);
		} else {
			if (parent != null) {
				// try the parent
				return parent.getConversionExecutor(sourceClass, targetClass);
			} else {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"No ConversionExecutor found for converting from sourceClass [" + sourceClass.getName()
								+ "] to target class [" + targetClass.getName() + "]");
			}
		}
	}

	public ConversionExecutor getConversionExecutor(String id, Class sourceClass, Class targetClass)
			throws ConversionExecutorNotFoundException {
		Assert.hasText(id, "The id of the custom converter is required");
		Assert.notNull(sourceClass, "The source class to convert from is required");
		Assert.notNull(targetClass, "The target class to convert to is required");
		sourceClass = convertToWrapperClassIfNecessary(sourceClass);
		targetClass = convertToWrapperClassIfNecessary(targetClass);
		if (targetClass.isAssignableFrom(sourceClass)) {
			return new StaticConversionExecutor(sourceClass, targetClass, new NoOpConverter(sourceClass, targetClass));
		}
		Converter converter = (Converter) customConverters.get(id);
		if (converter == null) {
			if (parent != null) {
				return parent.getConversionExecutor(id, sourceClass, targetClass);
			} else {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"No custom ConversionExecutor found with id '" + id + "' for converting from sourceClass ["
								+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
			}
		}
		if (converter.getSourceClass().isAssignableFrom(sourceClass)) {
			if (!converter.getTargetClass().isAssignableFrom(targetClass)) {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
								+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
			}
			return new StaticConversionExecutor(sourceClass, targetClass, converter);
		} else if (converter.getTargetClass().isAssignableFrom(sourceClass) && converter instanceof TwoWayConverter) {
			if (!converter.getSourceClass().isAssignableFrom(targetClass)) {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
								+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
			}
			TwoWayConverter twoWay = (TwoWayConverter) converter;
			return new StaticConversionExecutor(sourceClass, targetClass, new ReverseConverter(twoWay));
		} else {
			throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
					"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
							+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
		}
	}

	private Converter findRegisteredConverter(Class sourceClass, Class targetClass) {
		if (sourceClass.isInterface()) {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(sourceClass);
			while (!classQueue.isEmpty()) {
				Class currentClass = (Class) classQueue.removeLast();
				Map sourceTargetConverters = findConvertersForSource(currentClass);
				Converter converter = findTargetConverter(sourceTargetConverters, targetClass);
				if (converter != null) {
					return converter;
				}
				Class[] interfaces = currentClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					classQueue.addFirst(interfaces[i]);
				}
			}
			Map objectConverters = findConvertersForSource(Object.class);
			return findTargetConverter(objectConverters, targetClass);
		} else {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(sourceClass);
			while (!classQueue.isEmpty()) {
				Class currentClass = (Class) classQueue.removeLast();
				Map sourceTargetConverters = findConvertersForSource(currentClass);
				Converter converter = findTargetConverter(sourceTargetConverters, targetClass);
				if (converter != null) {
					return converter;
				}
				if (currentClass.getSuperclass() != null) {
					classQueue.addFirst(currentClass.getSuperclass());
				}
				Class[] interfaces = currentClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					classQueue.addFirst(interfaces[i]);
				}
			}
			return null;
		}
	}

	public Object executeConversion(Object source, Class targetClass) throws ConversionException {
		if (source != null) {
			ConversionExecutor conversionExecutor = getConversionExecutor(source.getClass(), targetClass);
			return conversionExecutor.execute(source);
		} else {
			return null;
		}
	}

	public Class getClassForAlias(String name) throws IllegalArgumentException {
		Class clazz = (Class) aliasMap.get(name);
		if (clazz != null) {
			return clazz;
		} else {
			if (parent != null) {
				return parent.getClassForAlias(name);
			} else {
				return null;
			}
		}
	}

	// subclassing support

	public Set getConversionExecutors(Class sourceClass) {
		Set parentExecutors;
		if (parent != null) {
			parentExecutors = parent.getConversionExecutors(sourceClass);
		} else {
			parentExecutors = Collections.EMPTY_SET;
		}
		Map sourceMap = getSourceMap(sourceClass);
		if (parentExecutors.isEmpty() && sourceMap.isEmpty()) {
			return Collections.EMPTY_SET;
		}
		Set entries = sourceMap.entrySet();
		Set conversionExecutors = new HashSet(entries.size() + parentExecutors.size());
		for (Iterator it = entries.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Class targetClass = (Class) entry.getKey();
			Converter converter = (Converter) entry.getValue();
			conversionExecutors.add(new StaticConversionExecutor(sourceClass, targetClass, converter));
		}
		conversionExecutors.addAll(parentExecutors);
		return conversionExecutors;
	}

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
		Map sourceConverters = (Map) sourceClassConverters.get(sourceClass);
		return sourceConverters != null ? sourceConverters : Collections.EMPTY_MAP;
	}

	private Converter findTargetConverter(Map sourceTargetConverters, Class targetClass) {
		if (sourceTargetConverters.isEmpty()) {
			return null;
		}
		if (targetClass.isInterface()) {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(targetClass);
			while (!classQueue.isEmpty()) {
				Class currentClass = (Class) classQueue.removeLast();
				Converter converter = (Converter) sourceTargetConverters.get(currentClass);
				if (converter != null) {
					return converter;
				}
				Class[] interfaces = currentClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					classQueue.addFirst(interfaces[i]);
				}
			}
			return (Converter) sourceTargetConverters.get(Object.class);
		} else {
			LinkedList classQueue = new LinkedList();
			classQueue.addFirst(targetClass);
			while (!classQueue.isEmpty()) {
				Class currentClass = (Class) classQueue.removeLast();
				Converter converter = (Converter) sourceTargetConverters.get(currentClass);
				if (converter != null) {
					return converter;
				}
				if (currentClass.getSuperclass() != null) {
					classQueue.addFirst(currentClass.getSuperclass());
				}
				Class[] interfaces = currentClass.getInterfaces();
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
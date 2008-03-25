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
package org.springframework.binding.convert.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.Converter;
import org.springframework.util.Assert;

/**
 * Base implementation of a conversion service. Initially empty, e.g. no converters are registered by default.
 * 
 * @author Keith Donald
 */
public class GenericConversionService implements ConversionService {

	/**
	 * An indexed map of converters. Each entry key is a source class that can be converted from, and each entry value
	 * is a map of target classes that can be convertered to, ultimately mapping to a specific converter that can
	 * perform the source->target conversion.
	 */
	private Map sourceClassConverters = new HashMap();

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
		Class[] sourceClasses = converter.getSourceClasses();
		Class[] targetClasses = converter.getTargetClasses();
		for (int i = 0; i < sourceClasses.length; i++) {
			Class sourceClass = sourceClasses[i];
			Map sourceMap = (Map) sourceClassConverters.get(sourceClass);
			if (sourceMap == null) {
				sourceMap = new HashMap();
				sourceClassConverters.put(sourceClass, sourceMap);
			}
			for (int j = 0; j < targetClasses.length; j++) {
				Class targetClass = targetClasses[j];
				sourceMap.put(targetClass, converter);
			}
		}
	}

	public ConversionExecutor getConversionExecutor(Class sourceClass, Class targetClass) throws ConversionException {
		Assert.notNull(sourceClass, "The source class to convert from is required");
		Assert.notNull(targetClass, "The target class to convert to is required");
		if (this.sourceClassConverters == null || this.sourceClassConverters.isEmpty()) {
			throw new IllegalStateException("No converters have been added to this service's registry");
		}
		sourceClass = convertToWrapperClassIfNecessary(sourceClass);
		targetClass = convertToWrapperClassIfNecessary(targetClass);
		if (targetClass.isAssignableFrom(sourceClass)) {
			return new StaticConversionExecutor(sourceClass, targetClass, new NoOpConverter(sourceClass, targetClass));
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
				throw new ConversionException(sourceClass, targetClass,
						"No converter registered to convert from sourceClass '" + sourceClass + "' to target class '"
								+ targetClass + "'");
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
		LinkedList classQueue = new LinkedList();
		classQueue.addFirst(sourceClass);
		while (!classQueue.isEmpty()) {
			sourceClass = (Class) classQueue.removeLast();
			Map sourceTargetConverters = (Map) sourceClassConverters.get(sourceClass);
			if (sourceTargetConverters != null && !sourceTargetConverters.isEmpty()) {
				return sourceTargetConverters;
			}
			if (!sourceClass.isInterface() && (sourceClass.getSuperclass() != null)) {
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

	private Converter findTargetConverter(Map sourceTargetConverters, Class targetClass) {
		LinkedList classQueue = new LinkedList();
		classQueue.addFirst(targetClass);
		while (!classQueue.isEmpty()) {
			targetClass = (Class) classQueue.removeLast();
			Converter converter = (Converter) sourceTargetConverters.get(targetClass);
			if (converter != null) {
				return converter;
			}
			if (!targetClass.isInterface() && (targetClass.getSuperclass() != null)) {
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
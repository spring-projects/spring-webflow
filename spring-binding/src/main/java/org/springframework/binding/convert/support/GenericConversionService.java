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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.Converter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

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
	 * A map of string aliases to convertible classes. Allows lookup of converters by alias.
	 */
	private Map aliasMap = new HashMap();

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
	 * Add given converter to this conversion service. If the converter is {@link ConversionServiceAware}, it will get
	 * the conversion service injected.
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
		if (converter instanceof ConversionServiceAware) {
			((ConversionServiceAware) converter).setConversionService(this);
		}
	}

	/**
	 * Add all given converters. If the converters are {@link ConversionServiceAware}, they will get the conversion
	 * service injected.
	 */
	public void addConverters(Converter[] converters) {
		for (int i = 0; i < converters.length; i++) {
			addConverter(converters[i]);
		}
	}

	/**
	 * Add given converter with an alias to the conversion service. If the converter is {@link ConversionServiceAware},
	 * it will get the conversion service injected.
	 */
	public void addConverter(Converter converter, String alias) {
		aliasMap.put(alias, converter);
		addConverter(converter);
	}

	/**
	 * Add an alias for given target type.
	 */
	public void addAlias(String alias, Class targetType) {
		Assert.isTrue(!targetType.isPrimitive(), "Primitive types cannot be registered");
		aliasMap.put(alias, targetType);
	}

	/**
	 * Generate a conventions based alias for given target type. For instance, "java.lang.Boolean" will get the
	 * "boolean" alias.
	 */
	public void addDefaultAlias(Class targetType) {
		addAlias(StringUtils.uncapitalize(ClassUtils.getShortName(targetType)), targetType);
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
			return new ConversionExecutorImpl(sourceClass, targetClass, new NoOpConverter(sourceClass, targetClass));
		}
		Map sourceTargetConverters = findConvertersForSource(sourceClass);
		Converter converter = findTargetConverter(sourceTargetConverters, targetClass);
		if (converter != null) {
			// we found a converter
			return new ConversionExecutorImpl(sourceClass, targetClass, converter);
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

	public ConversionExecutor getConversionExecutorByTargetAlias(Class sourceClass, String alias)
			throws IllegalArgumentException {
		Assert.notNull(sourceClass, "The source class to convert from is required");
		Assert.hasText(alias, "The target alias is required and must either be a type alias (e.g 'boolean') "
				+ "or a generic converter alias (e.g. 'bean') ");
		Object targetType = aliasMap.get(alias);
		if (targetType == null) {
			if (parent != null) {
				// try the parent
				return parent.getConversionExecutorByTargetAlias(sourceClass, alias);
			} else {
				// not aliased
				return null;
			}
		} else if (targetType instanceof Class) {
			return getConversionExecutor(sourceClass, (Class) targetType);
		} else {
			Assert.isInstanceOf(Converter.class, targetType, "Not a converter: ");
			Converter converter = (Converter) targetType;
			return new ConversionExecutorImpl(sourceClass, Object.class, converter);
		}
	}

	public ConversionExecutor[] getConversionExecutorsForSource(Class sourceClass) {
		Assert.notNull(sourceClass, "The source class to convert from is required");
		Map sourceTargetConverters = findConvertersForSource(sourceClass);
		if (sourceTargetConverters.isEmpty()) {
			if (parent != null) {
				// use the parent
				return parent.getConversionExecutorsForSource(sourceClass);
			} else {
				// no converters for source class
				return new ConversionExecutor[0];
			}
		} else {
			Set executors = new HashSet();
			if (parent != null) {
				executors.addAll(Arrays.asList(parent.getConversionExecutorsForSource(sourceClass)));
			}
			Iterator it = sourceTargetConverters.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				executors.add(new ConversionExecutorImpl(sourceClass, (Class) entry.getKey(), (Converter) entry
						.getValue()));
			}
			return (ConversionExecutor[]) executors.toArray(new ConversionExecutor[executors.size()]);
		}
	}

	public Class getClassByAlias(String alias) {
		Assert.hasText(alias, "The alias is required and must be a type alias (e.g 'boolean')");
		Object clazz = aliasMap.get(alias);
		if (clazz != null) {
			Assert.isInstanceOf(Class.class, clazz, "Not a Class alias '" + alias + "': ");
			return (Class) clazz;
		} else {
			if (parent != null) {
				// try parent service
				return parent.getClassByAlias(alias);
			} else {
				// alias does not index a class, return null
				return null;
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
	 * Returns a map of known aliases. Each entry key is a String alias and the associated value is either a target
	 * class or a converter.
	 */
	protected Map getAliasMap() {
		return aliasMap;
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
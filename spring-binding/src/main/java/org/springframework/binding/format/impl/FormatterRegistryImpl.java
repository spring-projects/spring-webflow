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
package org.springframework.binding.format.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterFactory;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.util.Assert;

/**
 * Base class for formatter factories. Manages the locale used by the produced formatters using Spring's
 * {@link org.springframework.context.i18n.LocaleContext} system.
 * 
 * @author Keith Donald
 */
public class FormatterRegistryImpl implements FormatterRegistry {

	private LocaleContext localeContext = new SimpleLocaleContext(Locale.getDefault());

	private Map formattersById = new HashMap();

	private Map formattersByClass = new HashMap();

	/**
	 * Sets the locale context to use if no locale has been bound to the current thread. Optional. Defaults to a
	 * {@link SimpleLocaleContext} holding the system default locale.
	 * @param localeContext the locale context
	 */
	public void setLocaleContext(LocaleContext localeContext) {
		this.localeContext = localeContext;
	}

	/**
	 * Returns the locale in use.
	 */
	protected Locale getLocale() {
		Locale currentLocale = LocaleContextHolder.getLocale();
		if (currentLocale != null) {
			return currentLocale;
		} else {
			return localeContext.getLocale();
		}
	}

	public Formatter getFormatter(Class clazz) {
		Assert.notNull(clazz, "The formatted class argument is required");
		clazz = convertToWrapperClassIfNecessary(clazz);
		FormatterFactory factory = findFormatterFactory(clazz);
		if (factory != null) {
			FormatterFactoryContextImpl context = new FormatterFactoryContextImpl();
			context.setLocale(getLocale());
			context.setFormattedClass(clazz);
			return factory.createFormatter(context);
		} else {
			return null;
		}
	}

	public Formatter getFormatter(String id, Class clazz) {
		Assert.hasText(id, "The id of the custom formatter is required");
		FormatterFactory factory = (FormatterFactory) formattersById.get(id);
		if (factory != null) {
			FormatterFactoryContextImpl context = new FormatterFactoryContextImpl();
			context.setLocale(getLocale());
			context.setFormattedClass(clazz);
			return factory.createFormatter(context);
		} else {
			return null;
		}
	}

	public void registerFormatter(String id, FormatterFactory factory) {
		Assert.hasText(id, "The id of the custom formatter is required");
		Assert.notNull(factory, "The formatter factory is required");
		formattersById.put(id, factory);
		Class formattedClass = factory.getFormattedClass();
		if (!formattersByClass.containsKey(formattedClass)) {
			formattersByClass.put(formattedClass, factory);
		}
	}

	public void registerFormatter(FormatterFactory factory) {
		Assert.notNull(factory, "The formatter factory is required");
		formattersByClass.put(factory.getFormattedClass(), factory);
	}

	private FormatterFactory findFormatterFactory(Class clazz) {
		LinkedList classQueue = new LinkedList();
		classQueue.addFirst(clazz);
		while (!classQueue.isEmpty()) {
			clazz = (Class) classQueue.removeLast();
			FormatterFactory factory = (FormatterFactory) formattersByClass.get(clazz);
			if (factory != null) {
				return factory;
			}
			if (!clazz.isInterface() && clazz.getSuperclass() != null) {
				classQueue.add(clazz.getSuperclass());
			}
			Class[] interfaces = clazz.getInterfaces();
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
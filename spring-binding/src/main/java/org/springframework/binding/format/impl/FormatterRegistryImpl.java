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
import java.util.Locale;
import java.util.Map;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterFactory;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;

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
		FormatterFactory factory = (FormatterFactory) formattersById.get(id);
		if (factory != null) {
			FormatterFactoryContextImpl context = new FormatterFactoryContextImpl();
			context.setLocale(getLocale());
			context.setFormattedClass(clazz);
			return factory.createFormatter(context);
		} else {
			return getFormatter(clazz);
		}
	}

	private FormatterFactory findFormatterFactory(Class clazz) {
		FormatterFactory factory = (FormatterFactory) formattersByClass.get(clazz);
		if (factory != null) {
			return factory;
		} else {
			// TODO - getSuperClass can return null
			if (clazz.getSuperclass() != Object.class) {
				return findFormatterFactory(clazz.getSuperclass());
			} else {
				return null;
			}
		}
	}

	public void registerFormatter(String id, FormatterFactory factory) {
		formattersById.put(id, factory);
		Class formattedClass = factory.getFormattedClass();
		if (!formattersByClass.containsKey(formattedClass)) {
			formattersByClass.put(formattedClass, factory);
		}
	}

	public void registerFormatter(FormatterFactory factory) {
		formattersByClass.put(factory.getFormattedClass(), factory);
	}
}
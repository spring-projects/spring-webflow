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
package org.springframework.binding.format.registry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.util.Assert;

/**
 * Base class for formatter factories. Manages the locale used by the produced formatters using Spring's
 * {@link org.springframework.context.i18n.LocaleContext} system.
 * 
 * @author Keith Donald
 */
public class GenericFormatterRegistry implements FormatterRegistry {

	private Map formattersById = new HashMap();

	private Map formattersByClass = new HashMap();

	public Formatter getFormatter(Class clazz) {
		Assert.notNull(clazz, "The formatted class argument is required");
		clazz = convertToWrapperClassIfNecessary(clazz);
		return findFormatter(clazz);
	}

	public Formatter getFormatter(String id) {
		Assert.hasText(id, "The id of the custom formatter is required");
		return (Formatter) formattersById.get(id);
	}

	// impl

	public void registerFormatter(Class clazz, Formatter formatter) {
		Assert.notNull(formatter, "The formatter to register is required");
		formattersByClass.put(clazz, formatter);
	}

	public void registerFormatter(String id, Formatter formatter) {
		Assert.hasText(id, "The id of the custom formatter is required");
		Assert.notNull(formatter, "The formatter to register is required");
		formattersById.put(id, formatter);
	}

	// helpers

	private Formatter findFormatter(Class clazz) {
		LinkedList classQueue = new LinkedList();
		classQueue.addFirst(clazz);
		while (!classQueue.isEmpty()) {
			clazz = (Class) classQueue.removeLast();
			Formatter factory = (Formatter) formattersByClass.get(clazz);
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
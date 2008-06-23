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
package org.springframework.binding.format.registry;

import java.util.HashMap;
import java.util.Map;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.util.Assert;

/**
 * A general-purpose {@link FormatterRegistry} implementation allows formatters to be registered programatically.
 * 
 * @see #registerFormatter(Formatter)
 * @see #registerFormatter(String, Formatter)
 * 
 * @author Keith Donald
 */
public class GenericFormatterRegistry implements FormatterRegistry {

	private Map formattersById = new HashMap();

	private Map formattersByClass = new HashMap();

	public Formatter getFormatter(Class clazz) {
		Assert.notNull(clazz, "The formatted class argument is required");
		clazz = convertToWrapperClassIfNecessary(clazz);
		return (Formatter) formattersByClass.get(clazz);
	}

	public Formatter getFormatter(Class clazz, String id) {
		Assert.notNull(clazz, "The formatted class argument is required");
		Assert.hasText(id, "The id of the custom formatter is required");
		Formatter formatter = (Formatter) formattersById.get(id);
		if (formatter != null && formatter.getObjectType().equals(clazz)) {
		}
		return formatter;
	}

	// impl

	public void registerFormatter(Formatter formatter) {
		Assert.notNull(formatter, "The formatter to register is required");
		formattersByClass.put(formatter.getObjectType(), formatter);
	}

	public void registerFormatter(String id, Formatter formatter) {
		Assert.hasText(id, "The id of the custom formatter is required");
		Assert.notNull(formatter, "The formatter to register is required");
		formattersById.put(id, formatter);
	}

	// helpers

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
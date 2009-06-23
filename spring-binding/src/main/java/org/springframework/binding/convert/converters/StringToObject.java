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

public abstract class StringToObject implements TwoWayConverter {

	private Class objectClass;

	public StringToObject(Class objectClass) {
		this.objectClass = objectClass;
	}

	public final Class getSourceClass() {
		return String.class;
	}

	public final Class getTargetClass() {
		return objectClass;
	}

	public final Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		String string = (String) source;
		if (string != null && string.length() > 0) {
			return toObject(string, targetClass);
		} else {
			return null;
		}
	}

	public final Object convertTargetToSourceClass(Object target, Class sourceClass) throws Exception {
		if (target != null) {
			return toString(target);
		} else {
			return "";
		}
	}

	protected abstract Object toObject(String string, Class targetClass) throws Exception;

	protected abstract String toString(Object object) throws Exception;

}
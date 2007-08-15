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

import org.springframework.binding.convert.ConversionContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Converts a textual representation of a class object to a <code>Class</code> instance.
 * 
 * @author Keith Donald
 */
public class TextToClass extends ConversionServiceAwareConverter {

	private static final String ALIAS_PREFIX = "type:";

	private static final String CLASS_PREFIX = "class:";

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { Class.class };
	}

	protected Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception {
		String text = (String) source;
		if (StringUtils.hasText(text)) {
			String classNameOrAlias = text.trim();
			if (classNameOrAlias.startsWith(CLASS_PREFIX)) {
				return ClassUtils.forName(text.substring(CLASS_PREFIX.length()));
			} else if (classNameOrAlias.startsWith(ALIAS_PREFIX)) {
				String alias = text.substring(ALIAS_PREFIX.length());
				Class clazz = getConversionService().getClassByAlias(alias);
				Assert.notNull(clazz, "No class found associated with type alias '" + alias + "'");
				return clazz;
			} else {
				// try first an aliased based lookup
				if (getConversionService() != null) {
					Class aliasedClass = getConversionService().getClassByAlias(classNameOrAlias);
					if (aliasedClass != null) {
						return aliasedClass;
					}
				}
				// treat as a class name
				return ClassUtils.forName(classNameOrAlias);
			}
		} else {
			return null;
		}
	}
}
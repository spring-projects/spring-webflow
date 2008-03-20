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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.enums.LabeledEnum;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Converts a textual representation of a class object to a <code>Class</code> instance.
 * 
 * @author Keith Donald
 */
public class TextToClass extends AbstractConverter {

	private Map aliasMap = new HashMap();

	public TextToClass() {
		addDefaultAliases();
	}

	/**
	 * Add an alias for given target type.
	 */
	public void addAlias(String alias, Class targetType) {
		aliasMap.put(alias, targetType);
	}

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { Class.class };
	}

	protected Object doConvert(Object source, Class targetClass, Object context) throws Exception {
		String text = (String) source;
		if (StringUtils.hasText(text)) {
			text = text.trim();
			if (aliasMap.containsKey(text)) {
				return aliasMap.get(text);
			} else {
				return ClassUtils.forName(text);
			}
		} else {
			return null;
		}
	}

	protected void addDefaultAliases() {
		addAlias("string", String.class);
		addAlias("short", Short.class);
		addAlias("integer", Integer.class);
		addAlias("int", Integer.class);
		addAlias("byte", Byte.class);
		addAlias("long", Long.class);
		addAlias("float", Float.class);
		addAlias("double", Double.class);
		addAlias("bigInteger", BigInteger.class);
		addAlias("bigDecimal", BigDecimal.class);
		addAlias("boolean", Boolean.class);
		addAlias("class", Class.class);
		addAlias("labeledEnum", LabeledEnum.class);
	}
}
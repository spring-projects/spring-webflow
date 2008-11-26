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
package org.springframework.webflow.engine.model;

import org.springframework.util.StringUtils;

/**
 * Model support for set actions.
 * <p>
 * Sets an attribute value in a scope.
 * 
 * @author Scott Andrews
 */
public class SetModel extends AbstractActionModel {

	private String name;

	private String value;

	private String type;

	/**
	 * Create a set action model
	 * @param name the name of the property to set
	 * @param value the value to set
	 */
	public SetModel(String name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if (StringUtils.hasText(name)) {
			this.name = name;
		} else {
			this.name = null;
		}
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		if (StringUtils.hasText(value)) {
			this.value = value;
		} else {
			this.value = null;
		}
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		if (StringUtils.hasText(type)) {
			this.type = type;
		} else {
			this.type = null;
		}
	}
}

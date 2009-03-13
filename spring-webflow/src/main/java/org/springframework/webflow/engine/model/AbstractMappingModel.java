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
 * Model support for mappings.
 * 
 * @author Scott Andrews
 */
public abstract class AbstractMappingModel extends AbstractModel {

	private String name;

	private String value;

	private String type;

	private String required;

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

	/**
	 * @return the required
	 */
	public String getRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(String required) {
		if (StringUtils.hasText(required)) {
			this.required = required;
		} else {
			this.required = null;
		}
	}
}

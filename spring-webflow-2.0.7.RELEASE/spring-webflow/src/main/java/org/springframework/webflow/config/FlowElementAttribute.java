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
package org.springframework.webflow.config;

import org.springframework.util.Assert;

/**
 * A low-level definition of a attribute describing a flow artifact.
 * 
 * @author Keith Donald
 */
class FlowElementAttribute {

	/**
	 * The name of the attribute.
	 */
	private String name;

	/**
	 * The value of the attribute before type-conversion.
	 */
	private String value;

	/**
	 * The attribute type, optional, but necessary for type conversion.
	 */
	private String type;

	public FlowElementAttribute(String name, String value, String type) {
		Assert.hasText(name, "The name is required");
		Assert.hasText(value, "The value is required");
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public boolean needsTypeConversion() {
		return type != null && type.length() > 0;
	}
}
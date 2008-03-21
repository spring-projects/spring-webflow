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
package org.springframework.webflow.engine.model;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Model support for attributes.
 * <p>
 * A meta attribute describing or otherwise annotating it's holder.
 * 
 * @author Scott Andrews
 */
public class AttributeModel extends AbstractModel {
	private String name;
	private String type;
	private String value;

	/**
	 * Create an attribute model
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	public AttributeModel(String name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * Create an attribute model
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 * @param type the type of the value
	 */
	public AttributeModel(String name, String value, String type) {
		setName(name);
		setValue(value);
		setType(type);
	}

	/**
	 * Merge properties
	 * @param model the attribute to merge into this attribute
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			AttributeModel attribute = (AttributeModel) model;
			setType(merge(getType(), attribute.getType()));
		}
	}

	/**
	 * Tests if the model is able to be merged with this attribute
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof AttributeModel)) {
			return false;
		}
		AttributeModel attribute = (AttributeModel) model;
		return ObjectUtils.nullSafeEquals(getName(), attribute.getName())
				&& ObjectUtils.nullSafeEquals(getValue(), attribute.getValue());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof AttributeModel)) {
			return false;
		}
		AttributeModel attribute = (AttributeModel) obj;
		if (attribute == null) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getName(), attribute.getName())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getType(), attribute.getType())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getValue(), attribute.getValue())) {
			return false;
		} else {
			return super.equals(attribute);
		}
	}

	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(getName()) * 27 + ObjectUtils.nullSafeHashCode(getType()) * 27
				+ ObjectUtils.nullSafeHashCode(getValue()) * 27 + super.hashCode() * 27;
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
}

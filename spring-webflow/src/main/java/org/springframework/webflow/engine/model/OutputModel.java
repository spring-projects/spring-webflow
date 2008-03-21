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

/**
 * Model support for output mappings.
 * <p>
 * Maps a single output attribute out of this flow or subflow.
 * 
 * @author Scott Andrews
 */
public class OutputModel extends AbstractMappingModel {

	/**
	 * Create an output mapping model
	 * @param name the name of the mapping variable
	 * @param value the value to map
	 */
	public OutputModel(String name, String value) {
		setName(name);
		setValue(value);
	}

	/**
	 * Create an output mapping model
	 * @param name the name of the mapping variable
	 * @param value the value to map
	 * @param type the type of the value
	 * @param required indicates if this mapping is required
	 */
	public OutputModel(String name, String value, String type, String required) {
		setName(name);
		setValue(value);
		setType(type);
		setRequired(required);
	}

	/**
	 * Merge properties
	 * @param model the mapping to merge into this mapping
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			OutputModel output = (OutputModel) model;
			setValue(merge(getValue(), output.getValue()));
			setType(merge(getType(), output.getType()));
			setRequired(merge(getRequired(), output.getRequired()));
		}
	}

	/**
	 * Tests if the model is able to be merged with this output mapping
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof OutputModel)) {
			return false;
		}
		OutputModel output = (OutputModel) model;
		return ObjectUtils.nullSafeEquals(getName(), output.getName());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof InputModel)) {
			return false;
		}
		OutputModel output = (OutputModel) obj;
		if (output == null) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getName(), output.getName())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getValue(), output.getValue())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getType(), output.getType())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getRequired(), output.getRequired())) {
			return false;
		} else {
			return true;
		}
	}

	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(getName()) * 27 + ObjectUtils.nullSafeHashCode(getValue()) * 27
				+ ObjectUtils.nullSafeHashCode(getType()) * 27 + ObjectUtils.nullSafeHashCode(getRequired()) * 27;
	}

}

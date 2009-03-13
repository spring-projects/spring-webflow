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

import org.springframework.util.ObjectUtils;

/**
 * Model support for input mappings.
 * <p>
 * Maps a single input attribute into this flow or subflow.
 * 
 * @author Scott Andrews
 */
public class InputModel extends AbstractMappingModel {

	/**
	 * Create an input mapping model
	 * @param name the name of the mapping variable
	 * @param value the value to map
	 */
	public InputModel(String name, String value) {
		setName(name);
		setValue(value);
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof InputModel)) {
			return false;
		}
		InputModel input = (InputModel) model;
		return ObjectUtils.nullSafeEquals(getName(), input.getName());
	}

	public void merge(Model model) {
		InputModel input = (InputModel) model;
		setValue(merge(getValue(), input.getValue()));
		setType(merge(getType(), input.getType()));
		setRequired(merge(getRequired(), input.getRequired()));
	}

}

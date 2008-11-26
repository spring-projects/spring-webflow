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
import org.springframework.util.StringUtils;

/**
 * Model support for binding elements.
 * @author Scott Andrews
 */
public class BindingModel extends AbstractModel {

	private String property;

	private String converter;

	private String required;

	/**
	 * Create a binding model
	 * @param property the name of the bound property
	 * @param converter the converter
	 * @param required required status
	 */
	public BindingModel(String property, String converter, String required) {
		setProperty(property);
		setConverter(converter);
		setRequired(required);
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof BindingModel)) {
			return false;
		}
		BindingModel binding = (BindingModel) model;
		return ObjectUtils.nullSafeEquals(getProperty(), binding.getProperty());
	}

	public void merge(Model model) {
		BindingModel binding = (BindingModel) model;
		setConverter(merge(getConverter(), binding.getConverter()));
		setRequired(merge(getRequired(), binding.getRequired()));
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		if (StringUtils.hasText(property)) {
			this.property = property;
		} else {
			this.property = null;
		}
	}

	public String getConverter() {
		return converter;
	}

	public void setConverter(String converter) {
		if (StringUtils.hasText(converter)) {
			this.converter = converter;
		} else {
			this.converter = null;
		}
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		if (StringUtils.hasText(required)) {
			this.required = required;
		} else {
			this.required = null;
		}
	}

}

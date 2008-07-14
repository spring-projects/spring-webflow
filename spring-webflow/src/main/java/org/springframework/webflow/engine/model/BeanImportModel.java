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
 * Model support for bean imports.
 * <p>
 * Imports user-defined beans defined at a resource location. These beans become part of the flow's bean factory and are
 * resolvable using flow expressions.
 * 
 * @author Scott Andrews
 */
public class BeanImportModel extends AbstractModel {

	private String resource;

	/**
	 * Create a bean import model
	 * @param resource the resource containing beans to import
	 */
	public BeanImportModel(String resource) {
		setResource(resource);
	}

	public boolean isMergeableWith(Model model) {
		return false;
	}

	public void merge(Model model) {

	}

	/**
	 * @return the resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(String resource) {
		if (StringUtils.hasText(resource)) {
			this.resource = resource;
		} else {
			this.resource = null;
		}
	}
}

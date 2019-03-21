/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.engine.model;

import java.util.LinkedList;

/**
 * Model support for actions.
 * 
 * @author Scott Andrews
 */
public abstract class AbstractActionModel extends AbstractModel {

	private LinkedList<AttributeModel> attributes;

	/**
	 * Actions are not mergeable
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		return false;
	}

	/**
	 * Actions are not mergeable
	 * @param model the render action to merge into this render
	 */
	public void merge(Model model) {
		// not mergeable
	}

	protected void fillCopy(AbstractActionModel copy) {
		copy.setAttributes(copyList(attributes));
	}

	/**
	 * @return the attributes
	 */
	public LinkedList<AttributeModel> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(LinkedList<AttributeModel> attributes) {
		this.attributes = attributes;
	}

}

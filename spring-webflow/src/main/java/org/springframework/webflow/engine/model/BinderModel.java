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

import java.util.LinkedList;

/**
 * Model support for the view-state binder element.
 * @author Scott Andrews
 */
public class BinderModel extends AbstractModel {

	private LinkedList bindings;

	public void addBinding(BindingModel bindingModel) {
		if (bindings == null) {
			bindings = new LinkedList();
		}
		bindings.add(bindingModel);
	}

	public LinkedList getBindings() {
		return bindings;
	}

	public void setBindings(LinkedList bindings) {
		this.bindings = bindings;
	}

	public boolean isMergeableWith(Model model) {
		return model instanceof BinderModel;
	}

	public void merge(Model model) {
		BinderModel binder = (BinderModel) model;
		setBindings(merge(getBindings(), binder.getBindings()));
	}

}
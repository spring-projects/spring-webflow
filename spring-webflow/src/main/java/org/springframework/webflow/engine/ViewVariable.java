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
package org.springframework.webflow.engine;

import org.springframework.webflow.core.AnnotatedObject;
import org.springframework.webflow.execution.RequestContext;

/**
 * A variable scoped to a particular view. Such a variable is allocated when a view-state is entered and destroyed when
 * that view-state exits. The flow scope map is used as the backing variable store.
 * 
 * @author Keith Donald
 */
public class ViewVariable extends AnnotatedObject {

	private String name;

	private VariableValueFactory valueFactory;

	/**
	 * Creates a new view variable.
	 * @param name the name of the variable
	 * @param valueFactory the source for the variable value
	 */
	public ViewVariable(String name, VariableValueFactory valueFactory) {
		this.name = name;
		this.valueFactory = valueFactory;
	}

	/**
	 * Returns the name of this view variable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the source of the variable's initial value.
	 */
	public VariableValueFactory getValueFactory() {
		return valueFactory;
	}

	// name and scope based equality

	public boolean equals(Object o) {
		if (!(o instanceof ViewVariable)) {
			return false;
		}
		ViewVariable other = (ViewVariable) o;
		return name.equals(other.name) && valueFactory.equals(other.valueFactory);
	}

	public int hashCode() {
		return name.hashCode() + valueFactory.hashCode();
	}

	/**
	 * Creates this view variable. This method allocates the variable's value in flow scope.
	 * @param context the executing flow
	 */
	public void create(RequestContext context) {
		Object value = valueFactory.createInitialValue(context);
		context.getViewScope().put(name, value);
	}

	/**
	 * Restores this view variable's dependencies. This method asks the variable's value factory to restore any
	 * references the variable has to transient objects.
	 * @param context the executing flow
	 */
	public void restore(RequestContext context) {
		Object value = context.getViewScope().get(name);
		valueFactory.restoreReferences(value, context);
	}

	/**
	 * Destroy this view variable. This method removes the variable's value in flow scope.
	 * @param context the executing flow
	 */
	public Object destroy(RequestContext context) {
		return context.getViewScope().remove(name);
	}
}

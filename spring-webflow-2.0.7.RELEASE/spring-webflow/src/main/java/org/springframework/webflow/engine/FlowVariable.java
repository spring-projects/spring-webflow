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

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.AnnotatedObject;
import org.springframework.webflow.execution.RequestContext;

/**
 * A value object that defines a specification for a flow variable. Such a variable is allocated when a flow starts and
 * destroyed when that flow ends. This class encapsulates information about the variable and the behavior necessary to
 * allocate the variable instance in flow scope.
 * 
 * @author Keith Donald
 */
public class FlowVariable extends AnnotatedObject {

	/**
	 * The variable name.
	 */
	private String name;

	/**
	 * The value factory that provides this variable's value.
	 */
	private VariableValueFactory valueFactory;

	/**
	 * Creates a new flow variable.
	 * @param name the variable name
	 */
	public FlowVariable(String name, VariableValueFactory valueFactory) {
		Assert.hasText(name, "The variable name is required");
		Assert.notNull(valueFactory, "The variable value factory is required");
		this.name = name;
		this.valueFactory = valueFactory;
	}

	/**
	 * Returns the name of this variable.
	 */
	public String getName() {
		return name;
	}

	// name and scope based equality

	public boolean equals(Object o) {
		if (!(o instanceof FlowVariable)) {
			return false;
		}
		FlowVariable other = (FlowVariable) o;
		return name.equals(other.name) && valueFactory.equals(other.valueFactory);
	}

	public int hashCode() {
		return name.hashCode() + valueFactory.hashCode();
	}

	/**
	 * Creates this flow variable. This method allocates the variable's value in the correct flow scope.
	 * @param context the executing flow
	 */
	public void create(RequestContext context) {
		Object value = valueFactory.createInitialValue(context);
		context.getFlowScope().put(name, value);
	}

	/**
	 * Restores this variable's dependencies. This method asks the variable's value factory to restore any references
	 * the variable has to transient objects.
	 * @param context the executing flow
	 */
	public void restore(RequestContext context) {
		Object value = context.getFlowScope().get(name);
		valueFactory.restoreReferences(value, context);
	}

	/**
	 * Destroys this flow variable. This method removes the variable's value in the correct flow scope.
	 * @param context the executing flow
	 */
	public Object destroy(RequestContext context) {
		return context.getFlowScope().remove(name);
	}

	public String toString() {
		return new ToStringCreator(this).append("name", name).append("valueFactory", valueFactory).toString();
	}
}
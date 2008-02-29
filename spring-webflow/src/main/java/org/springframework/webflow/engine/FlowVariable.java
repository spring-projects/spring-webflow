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
package org.springframework.webflow.engine;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;

/**
 * A value object that defines a specification for a flow variable. Encapsulates information about the variable and the
 * behavior necessary to create a new variable instance in a flow execution scope.
 * 
 * @author Keith Donald
 */
public class FlowVariable extends AnnotatedObject {

	/**
	 * The variable name.
	 */
	private String name;

	/**
	 * Is this flow variable local or global? Local variables go into flow scope. Global variables go into conversation
	 * scope.
	 */
	private Boolean local;

	/**
	 * The value factory that provides this variable's value.
	 */
	private VariableValueFactory valueFactory;

	/**
	 * Creates a new flow variable.
	 * @param name the variable name
	 * @param local the local variable
	 */
	public FlowVariable(String name, VariableValueFactory valueFactory, boolean local) {
		Assert.hasText(name, "The variable name is required");
		Assert.notNull(valueFactory, "The variable value factory is required");
		this.name = name;
		this.valueFactory = valueFactory;
		this.local = Boolean.valueOf(local);
	}

	/**
	 * Returns the name of this variable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Is this a local flow variable or a conversation-scoped flow variable?
	 */
	public boolean isLocal() {
		return local.booleanValue();
	}

	// name and scope based equality

	public boolean equals(Object o) {
		if (!(o instanceof FlowVariable)) {
			return false;
		}
		FlowVariable other = (FlowVariable) o;
		return name.equals(other.name) && valueFactory.equals(other.valueFactory) && local.equals(other.local);
	}

	public int hashCode() {
		return name.hashCode() + valueFactory.hashCode() + local.hashCode();
	}

	/**
	 * Creates a new instance of this flow variable in the configured scope.
	 * @param context the flow execution request context
	 */
	public final void create(RequestContext context) {
		Object value = valueFactory.createVariableValue(context);
		if (local == Boolean.TRUE) {
			context.getFlowScope().put(name, value);
		} else {
			context.getConversationScope().put(name, value);
		}
	}

	public final Object restore(FlowSession session, RequestContext context) {
		Object value;
		if (local == Boolean.TRUE) {
			value = session.getScope().get(name);
		} else {
			value = context.getConversationScope().get(name);
		}
		return valueFactory.restoreReferences(value, context);
	}

	public String toString() {
		return new ToStringCreator(this).append("name", name).append("valueFactory", valueFactory).append("local",
				local).toString();
	}
}
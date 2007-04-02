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
package org.springframework.webflow.executor.jsf;

import java.util.Map;

import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;

import org.springframework.webflow.execution.FlowExecution;

/**
 * Custom property resolver that resolves supported properties of the current flow execution.
 * Supports resolving all scopes as java.util.Maps: "flowScope", "conversationScope", and "flashScope".
 * Also supports attribute searching when no scope prefix is specified. The search order is 
 * flash, flow, conversation.
 * 
 * @author Keith Donald
 */
public class FlowExecutionPropertyResolver extends PropertyResolver {

	/**
	 * The name of the special flash scope execution property.
	 */
	private static final String FLASH_SCOPE_PROPERTY = "flashScope";

	/**
	 * The name of the special flow scope execution property.
	 */
	private static final String FLOW_SCOPE_PROPERTY = "flowScope";

	/**
	 * The name of the special conversation scope execution property.
	 */
	private static final String CONVERSATION_SCOPE_PROPERTY = "conversationScope";

	/**
	 * The standard property resolver to delegate to if this one doesn't apply.
	 */
	private final PropertyResolver resolverDelegate;

	/**
	 * Create a new FlowExecutionPropertyResolver using the original PropertyResolver.
	 * <p>
	 * A JSF implementation will automatically pass its original resolver into the constructor of a configured resolver,
	 * provided that there is a corresponding constructor argument.
	 * 
	 * @param resolverDelegate the original VariableResolver
	 */
	public FlowExecutionPropertyResolver(PropertyResolver resolverDelegate) {
		this.resolverDelegate = resolverDelegate;
	}

	public Class getType(Object base, int index) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getType(base, index);
		}
		else {
			// can't access flow execution property by index - return null
			return null;
		}
	}

	public Class getType(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getType(base, property);
		}
		if (property == null) {
			throw new PropertyNotFoundException("Unable to get value from flow execution - property (key) is null");
		}
		if (!(property instanceof String)) {
			throw new PropertyNotFoundException("Unable to get value from flow execution - key is non-String");
		}
		if (FLASH_SCOPE_PROPERTY.equals(property)) {
			return Map.class;
		} else if (FLOW_SCOPE_PROPERTY.equals(property)) {
			return Map.class;
		} else if (CONVERSATION_SCOPE_PROPERTY.equals(property)) {
			return Map.class;
		} else {
			// perform an attribute search
			FlowExecution execution = (FlowExecution)base;
			String attributeName = (String)property;
			// try flash scope
			Object value = execution.getActiveSession().getFlashMap().get(attributeName);
			if (value != null) {
				return value.getClass();
			}
			// try flow scope
			value = execution.getActiveSession().getScope().get(attributeName);
			if (value != null) {
				return value.getClass();
			}
			// try conversation scope
			value = execution.getConversationScope().get(attributeName);
			if (value != null) {
				return value.getClass();
			}
			return null;
		}		
	}

	public Object getValue(Object base, int index) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getValue(base, index);
		}
		else {
			throw new ReferenceSyntaxException("Cannot apply an index value to flow execution");
		}
	}

	public Object getValue(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.getValue(base, property);
		}
		if (!(property instanceof String)) {
			throw new PropertyNotFoundException("Unable to get value from flow execution - key is non-String");
		}
		FlowExecution execution = (FlowExecution) base;
		if (FLASH_SCOPE_PROPERTY.equals(property)) {
			return execution.getActiveSession().getFlashMap().asMap();
		} else if (FLOW_SCOPE_PROPERTY.equals(property)) {
			return execution.getActiveSession().getFlashMap().asMap();
		} else if (CONVERSATION_SCOPE_PROPERTY.equals(property)) {
			return execution.getConversationScope().asMap();
		} else {
			// perform an attribute search
			String attributeName = (String)property;
			// try flash scope
			Object value = execution.getActiveSession().getFlashMap().get(attributeName);
			if (value != null) {
				return value;
			}
			// try flow scope
			value = execution.getActiveSession().getScope().get(attributeName);
			if (value != null) {
				return value;
			}
			// try conversation scope
			value = execution.getConversationScope().get(attributeName);
			if (value != null) {
				return value;
			}
			throw new PropertyNotFoundException("Cannot resolve flow execution property '" + property + "'");
		}
	}

	public boolean isReadOnly(Object base, int index) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.isReadOnly(base, index);
		}
		return false;
	}

	public boolean isReadOnly(Object base, Object property) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			return resolverDelegate.isReadOnly(base, property);
		}
		return false;
	}

	public void setValue(Object base, int index, Object value) throws EvaluationException, PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			resolverDelegate.setValue(base, index, value);
		}
		throw new ReferenceSyntaxException("Cannot apply an index value to a flow execution");
	}

	public void setValue(Object base, Object property, Object value) throws EvaluationException,
			PropertyNotFoundException {
		if (!(base instanceof FlowExecution)) {
			resolverDelegate.setValue(base, property, value);
			return;
		}
		if (property == null || !(property instanceof String)
				|| ((String)property).length() == 0) {
			throw new PropertyNotFoundException(
					"Attempt to set flow execution attribute with null name, empty name, or non-String name");
		}
		FlowExecution execution = (FlowExecution)base;
		String attributeName = (String)property;
		// perform a search: flash, flow, conversation
		if (execution.getActiveSession().getFlashMap().contains(attributeName)) {
			execution.getActiveSession().getFlashMap().put(attributeName, value);
		}
		else if (execution.getActiveSession().getScope().contains(attributeName)) {
			execution.getActiveSession().getScope().put(attributeName, value);
		} else if (execution.getConversationScope().contains(attributeName)) {
			execution.getConversationScope().put(attributeName, value);
		} else {
			throw new PropertyNotFoundException("Settable flow execution property '" + property + "' not found");
		}
	}
}
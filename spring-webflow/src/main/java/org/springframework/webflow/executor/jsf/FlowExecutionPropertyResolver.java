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

import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;

import org.springframework.webflow.execution.FlowExecution;

/**
 * Custom property resolver that resolves supported properties of the current flow execution.
 * Supports resolving all scopes as java.util.Maps: "flowScope", "conversationScope", and "flashScope".
 * Also supports attribute searching when no scope prefix is specified. The search order is 
 * flash, flow, conversation.
 * 
 * @author Keith Donald
 */
public class FlowExecutionPropertyResolver extends AbstractFlowExecutionPropertyResolver {

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
	 * Creates a new flow executon property resolver that resolves flash, flow, and conversation scope attributes.
	 * @param resolverDelegate the resolver to delegate to when the property is not a flow execution attribute
	 */
	public FlowExecutionPropertyResolver(PropertyResolver resolverDelegate) {
		super(resolverDelegate);
	}

	protected Class doGetAttributeType(FlowExecution execution, String attributeName) {
		if (FLASH_SCOPE_PROPERTY.equals(attributeName)) {
			return Map.class;
		} else if (FLOW_SCOPE_PROPERTY.equals(attributeName)) {
			return Map.class;
		} else if (CONVERSATION_SCOPE_PROPERTY.equals(attributeName)) {
			return Map.class;
		} else {
			// perform an attribute search
			
			// try flash scope first
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
			// cannot determine
			return null;
		}		
	}

	protected Object doGetAttribute(FlowExecution execution, String attributeName) {
		if (FLASH_SCOPE_PROPERTY.equals(attributeName)) {
			return execution.getActiveSession().getFlashMap().asMap();
		} else if (FLOW_SCOPE_PROPERTY.equals(attributeName)) {
			return execution.getActiveSession().getScope().asMap();
		} else if (CONVERSATION_SCOPE_PROPERTY.equals(attributeName)) {
			return execution.getConversationScope().asMap();
		} else {
			// perform an attribute search
			
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
			// cannot resolve as expected
			throw new PropertyNotFoundException("Readable flow execution attribute '" + attributeName + "' not found in any scope (flash, flow, or conversation)");
		}		
	}

	protected void doSetAttribute(FlowExecution execution, String attributeName, Object attributeValue) {
		// perform a search
		if (execution.getActiveSession().getFlashMap().contains(attributeName)) {
			execution.getActiveSession().getFlashMap().put(attributeName, attributeValue);
		}
		else if (execution.getActiveSession().getScope().contains(attributeName)) {
			execution.getActiveSession().getScope().put(attributeName, attributeValue);
		} else if (execution.getConversationScope().contains(attributeName)) {
			execution.getConversationScope().put(attributeName, attributeValue);
		} else {
			// cannot resolve as expected
			throw new PropertyNotFoundException("Settable flow execution attribute '" + attributeName + "' not found in any scope (flash, flow, or conversation)");
		}		
	}
}
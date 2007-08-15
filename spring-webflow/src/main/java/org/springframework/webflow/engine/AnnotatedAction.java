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
import org.springframework.util.StringUtils;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * An action proxy/decorator that stores arbitrary properties about a target <code>Action</code> implementation for
 * use within a specific Action execution context, for example an <code>ActionState</code> definition, a
 * <code>TransitionCriteria</code> definition, or in a test environment.
 * <p>
 * An annotated action is an action that wraps another action (referred to as the <i>target</i> action), setting up the
 * target action's execution attributes before invoking {@link Action#execute}.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class AnnotatedAction extends AnnotatedObject implements Action {

	// well known attributes

	/**
	 * The action name attribute ("name").
	 * <p>
	 * The name attribute is often used as a qualifier for an action's result event, and is typically used to allow the
	 * flow to respond to a specific action's outcome within a larger action execution chain.
	 * @see ActionState
	 */
	public static final String NAME_ATTRIBUTE = "name";

	/**
	 * The action execution method attribute ("method").
	 * <p>
	 * The method property is a hint about what method should be invoked; for example, the name of a specific target
	 * method on a {@link org.springframework.webflow.action.MultiAction multi action}.
	 * @see ActionState
	 */
	public static final String METHOD_ATTRIBUTE = "method";

	/**
	 * The target action to execute.
	 */
	private Action targetAction;

	/**
	 * Creates a new annotated action object for the specified action. No contextual properties are provided.
	 * @param targetAction the action
	 */
	public AnnotatedAction(Action targetAction) {
		setTargetAction(targetAction);
	}

	/**
	 * Returns the wrapped target action.
	 * @return the action
	 */
	public Action getTargetAction() {
		return targetAction;
	}

	/**
	 * Set the target action wrapped by this decorator.
	 */
	public void setTargetAction(Action targetAction) {
		Assert.notNull(targetAction, "The targetAction to annotate is required");
		this.targetAction = targetAction;
	}

	/**
	 * Returns the name of a named action, or <code>null</code> if the action is unnamed. Used when mapping action
	 * result events to transitions.
	 * @see #isNamed()
	 * @see #postProcessResult(Event)
	 */
	public String getName() {
		return getAttributeMap().getString(NAME_ATTRIBUTE);
	}

	/**
	 * Sets the name of a named action. This is optional and can be <code>null</code>.
	 * @param name the action name
	 */
	public void setName(String name) {
		getAttributeMap().put(NAME_ATTRIBUTE, name);
	}

	/**
	 * Returns whether or not the wrapped target action is a named action.
	 * @see #getName()
	 * @see #setName(String)
	 */
	public boolean isNamed() {
		return StringUtils.hasText(getName());
	}

	/**
	 * Returns the name of the action method to invoke when the target action is executed.
	 */
	public String getMethod() {
		return getAttributeMap().getString(METHOD_ATTRIBUTE);
	}

	/**
	 * Sets the name of the action method to invoke when the target action is executed.
	 * @param method the action method name
	 */
	public void setMethod(String method) {
		getAttributeMap().put(METHOD_ATTRIBUTE, method);
	}

	/**
	 * Set an attribute on this annotated object.
	 * @param attributeName the name of the attribute to set
	 * @param attributeValue the value of the attribute
	 * @return this object, to support call chaining
	 * @since 1.0.4
	 */
	public AnnotatedAction putAttribute(String attributeName, Object attributeValue) {
		getAttributeMap().put(attributeName, attributeValue);
		return this;
	}

	public Event execute(RequestContext context) throws Exception {
		AttributeMap originalAttributes = getAttributeMap();
		try {
			context.setAttributes(getAttributeMap());
			Event result = getTargetAction().execute(context);
			return postProcessResult(result);
		} finally {
			// restore original attributes
			context.setAttributes(originalAttributes);
		}
	}

	/**
	 * Get the event id to be used as grounds for a transition in the containing state, based on given result returned
	 * from action execution.
	 * <p>
	 * If the wrapped action is named, the name will be used as a qualifier for the event (e.g. "myAction.success").
	 * @param resultEvent the action result event
	 */
	protected Event postProcessResult(Event resultEvent) {
		if (resultEvent == null) {
			return null;
		}
		if (isNamed()) {
			// qualify result event id with action name for a named action
			String qualifiedId = getName() + "." + resultEvent.getId();
			resultEvent = new Event(resultEvent.getSource(), qualifiedId, resultEvent.getAttributes());
		}
		return resultEvent;
	}

	public String toString() {
		return new ToStringCreator(this).append("targetAction", getTargetAction()).append("attributes",
				getAttributeMap()).toString();
	}
}
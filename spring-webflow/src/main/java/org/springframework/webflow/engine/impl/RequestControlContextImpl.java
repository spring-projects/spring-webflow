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
package org.springframework.webflow.engine.impl;

import org.springframework.binding.message.MessageContext;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowSession;

/**
 * Default request control context implementation used internally by the web flow system. This class is closely coupled
 * with <code>FlowExecutionImpl</code> and <code>FlowSessionImpl</code>. The three classes work together to form a
 * complete flow execution implementation based on a finite state machine.
 * 
 * @see FlowExecutionImpl
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
class RequestControlContextImpl implements RequestControlContext {

	/**
	 * The owning flow execution carrying out this request.
	 */
	private FlowExecutionImpl flowExecution;

	/**
	 * A source context for the caller who initiated this request.
	 */
	private ExternalContext externalContext;

	/**
	 * A source context for messages to record during this flow execution request.
	 */
	private MessageContext messageContext;

	/**
	 * The request scope data map. Never null, initially empty.
	 */
	private LocalAttributeMap requestScope = new LocalAttributeMap();

	/**
	 * Holder for contextual properties describing the currently executing request; never null, initially empty and
	 * immutable.
	 */
	private AttributeMap attributes = CollectionUtils.EMPTY_ATTRIBUTE_MAP;

	/**
	 * The last event that occurred in this request context; initially null.
	 */
	private Event lastEvent;

	/**
	 * The last transition that executed in this request context; initially null.
	 */
	private Transition lastTransition;

	/**
	 * Create a new request context.
	 * @param flowExecution the owning flow execution
	 * @param externalContext the external context that originated the flow execution request
	 * @param messageContext the message context for recording status or validation messages during the execution of
	 * this request
	 */
	public RequestControlContextImpl(FlowExecutionImpl flowExecution, ExternalContext externalContext,
			MessageContext messageContext) {
		this.flowExecution = flowExecution;
		this.externalContext = externalContext;
		this.messageContext = messageContext;
	}

	// implementing RequestContext

	public FlowDefinition getActiveFlow() {
		return flowExecution.getActiveSession().getDefinition();
	}

	public StateDefinition getCurrentState() {
		return flowExecution.getActiveSession().getState();
	}

	public MutableAttributeMap getRequestScope() {
		return requestScope;
	}

	public MutableAttributeMap getFlashScope() {
		return flowExecution.getFlashScope();
	}

	public MutableAttributeMap getFlowScope() {
		return flowExecution.getActiveSession().getScope();
	}

	public MutableAttributeMap getConversationScope() {
		return flowExecution.getConversationScope();
	}

	public ParameterMap getRequestParameters() {
		return externalContext.getRequestParameterMap();
	}

	public ExternalContext getExternalContext() {
		return externalContext;
	}

	public MessageContext getMessageContext() {
		return messageContext;
	}

	public FlowExecutionContext getFlowExecutionContext() {
		return flowExecution;
	}

	public Event getLastEvent() {
		return lastEvent;
	}

	public TransitionDefinition getLastTransition() {
		return lastTransition;
	}

	public AttributeMap getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributeMap attributes) {
		if (attributes == null) {
			this.attributes = CollectionUtils.EMPTY_ATTRIBUTE_MAP;
		} else {
			this.attributes = attributes;
		}
	}

	// implementing RequestControlContext

	public String getFlowExecutionUrl() {
		String key = flowExecution.getKey() != null ? flowExecution.getKey().toString() : null;
		if (key != null) {
			return externalContext.getFlowExecutionUri(flowExecution.getDefinition().getId(), key);
		} else {
			return null;
		}
	}

	public void sendFlowExecutionRedirect() {
	}

	public void setCurrentState(State state) {
		flowExecution.setCurrentState(state, this);
	}

	public void setLastTransition(Transition lastTransition) {
		this.lastTransition = lastTransition;
	}

	public FlowExecutionKey assignFlowExecutionKey() {
		return flowExecution.assignKey();
	}

	public boolean getAlwaysRedirectOnPause() {
		Boolean redirectOnPause = flowExecution.getAttributes().getBoolean("alwaysRedirectOnPause");
		return redirectOnPause != null ? redirectOnPause.booleanValue() : false;
	}

	public void start(Flow flow, MutableAttributeMap input) throws FlowExecutionException {
		flowExecution.start(flow, input, this);
	}

	public void handleEvent(Event event) throws FlowExecutionException {
		this.lastEvent = event;
		flowExecution.handleEvent(event, this);
	}

	public void execute(Transition transition) {
		flowExecution.execute(transition, this);
	}

	public FlowSession endActiveFlowSession(MutableAttributeMap output) throws IllegalStateException {
		return flowExecution.endActiveFlowSession(output, this);
	}

	public String toString() {
		return new ToStringCreator(this).append("externalContext", externalContext)
				.append("requestScope", requestScope).append("attributes", attributes).append("flowExecution",
						flowExecution).toString();
	}
}
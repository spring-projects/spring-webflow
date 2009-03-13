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
package org.springframework.webflow.engine.impl;

import org.springframework.binding.message.MessageContext;
import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.context.ExternalContext;
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
import org.springframework.webflow.execution.View;

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
	 * Holder for contextual properties describing the currently executing request; never null, initially empty.
	 */
	private LocalAttributeMap attributes = new LocalAttributeMap();

	/**
	 * The current event being processed by this flow; initially null.
	 */
	private Event currentEvent;

	/**
	 * The last transition that executed in this request context; initially null.
	 */
	private Transition currentTransition;

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

	public TransitionDefinition getMatchingTransition(String eventId) throws IllegalStateException {
		return flowExecution.getMatchingTransition(eventId);
	}

	public MutableAttributeMap getRequestScope() {
		return requestScope;
	}

	public MutableAttributeMap getFlashScope() {
		return flowExecution.getFlashScope();
	}

	public boolean inViewState() {
		return flowExecution.isActive() && getCurrentState() != null && getCurrentState().isViewState();
	}

	public MutableAttributeMap getViewScope() throws IllegalStateException {
		return flowExecution.getActiveSession().getViewScope();
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

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public TransitionDefinition getCurrentTransition() {
		return currentTransition;
	}

	public MutableAttributeMap getAttributes() {
		return attributes;
	}

	// implementing RequestControlContext

	public String getFlowExecutionUrl() {
		String key = flowExecution.getKey() != null ? flowExecution.getKey().toString() : null;
		if (key != null) {
			return externalContext.getFlowExecutionUrl(flowExecution.getDefinition().getId(), key);
		} else {
			return null;
		}
	}

	public void sendFlowExecutionRedirect() {
		externalContext.requestFlowExecutionRedirect();
	}

	public void setCurrentState(State state) {
		flowExecution.setCurrentState(state, this);
	}

	public FlowExecutionKey assignFlowExecutionKey() {
		return flowExecution.assignKey();
	}

	public void viewRendering(View view) {
		flowExecution.viewRendering(view, this);
	}

	public void viewRendered(View view) {
		flowExecution.viewRendered(view, this);
	}

	public boolean handleEvent(Event event) throws FlowExecutionException {
		this.currentEvent = event;
		return flowExecution.handleEvent(event, this);
	}

	public boolean execute(Transition transition) {
		return flowExecution.execute(transition, this);
	}

	public void setCurrentTransition(Transition transition) {
		this.currentTransition = transition;
	}

	public void updateCurrentFlowExecutionSnapshot() {
		flowExecution.updateCurrentFlowExecutionSnapshot();
	}

	public void removeCurrentFlowExecutionSnapshot() {
		flowExecution.removeCurrentFlowExecutionSnapshot();
	}

	public void removeAllFlowExecutionSnapshots() {
		flowExecution.removeAllFlowExecutionSnapshots();
	}

	public void start(Flow flow, MutableAttributeMap input) throws FlowExecutionException {
		flowExecution.start(flow, input, this);
	}

	public void endActiveFlowSession(String outcome, MutableAttributeMap output) throws IllegalStateException {
		flowExecution.endActiveFlowSession(outcome, output, this);
	}

	public boolean getRedirectOnPause() {
		Boolean redirectOnPause = flowExecution.getAttributes().getBoolean("alwaysRedirectOnPause");
		return redirectOnPause != null ? redirectOnPause.booleanValue() : false;
	}

	public String toString() {
		return new ToStringCreator(this).append("externalContext", externalContext)
				.append("currentEvent", currentEvent).append("requestScope", requestScope).append("attributes",
						attributes).append("messageContext", messageContext).append("flowExecution", flowExecution)
				.toString();
	}
}
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
package org.springframework.webflow.test;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.FlowSession;

/**
 * A stub implementation of the flow execution context interface.
 * 
 * @see FlowExecutionContext
 * 
 * @author Keith Donald
 */
public class MockFlowExecutionContext implements FlowExecutionContext {

	private boolean started;

	private FlowExecutionKey key;

	private FlowDefinition flow;

	private FlowSession activeSession;

	private MutableAttributeMap flashScope = new LocalAttributeMap();

	private MutableAttributeMap conversationScope = new LocalAttributeMap();

	private MutableAttributeMap attributes = new LocalAttributeMap();

	private FlowExecutionOutcome outcome;

	/**
	 * Creates a new mock flow execution context -- automatically installs a root flow definition and active flow
	 * session.
	 */
	public MockFlowExecutionContext() {
		setActiveSession(new MockFlowSession());
		this.flow = activeSession.getDefinition();
	}

	/**
	 * Creates a new mock flow execution context for the specified root flow definition.
	 */
	public MockFlowExecutionContext(Flow flow) {
		this(new MockFlowSession(flow));
	}

	/**
	 * Creates a new mock flow execution context for the specified active flow session.
	 */
	public MockFlowExecutionContext(FlowSession flowSession) {
		setActiveSession(flowSession);
		this.flow = flowSession.getDefinition();
	}

	public FlowExecutionKey getKey() {
		return key;
	}

	public String getCaption() {
		return flow.getCaption();
	}

	// implementing flow execution context

	public FlowDefinition getDefinition() {
		return flow;
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean isActive() {
		return activeSession != null;
	}

	public boolean hasEnded() {
		return hasStarted() && !isActive();
	}

	public FlowSession getActiveSession() throws IllegalStateException {
		if (activeSession == null) {
			throw new IllegalStateException("No flow session is active");
		}
		return activeSession;
	}

	public MutableAttributeMap getFlashScope() {
		return flashScope;
	}

	public MutableAttributeMap getConversationScope() {
		return conversationScope;
	}

	public AttributeMap getAttributes() {
		return attributes;
	}

	public FlowExecutionOutcome getOutcome() {
		return outcome;
	}

	// convenience mock accessors

	public Flow getDefinitionInternal() {
		return (Flow) getDefinition();
	}

	// mutators

	/**
	 * Sets the top-level flow definition.
	 */
	public void setFlow(FlowDefinition rootFlow) {
		this.flow = rootFlow;
	}

	/**
	 * Sets the flow execution key
	 */
	public void setKey(FlowExecutionKey key) {
		this.key = key;
	}

	/**
	 * Sets the mock session to be the <i>active session</i>.
	 */
	public void setActiveSession(FlowSession activeSession) {
		this.activeSession = activeSession;
		if (!started && activeSession != null) {
			started = true;
		}
	}

	/**
	 * Sets the flow execution flash scope.
	 */
	public void setFlashScope(MutableAttributeMap scope) {
		this.flashScope = scope;
	}

	/**
	 * Sets the flow execution conversation scope.
	 */
	public void setConversationScope(MutableAttributeMap scope) {
		this.conversationScope = scope;
	}

	/**
	 * Sets the result of this flow ending.
	 * @param outcome the ended outcome
	 */
	public void setOutcome(FlowExecutionOutcome outcome) {
		this.outcome = outcome;
	}

	// convenience accessors

	/**
	 * Returns the mock active flow session.
	 */
	public MockFlowSession getMockActiveSession() {
		return (MockFlowSession) activeSession;
	}

	/**
	 * Returns the mutable execution attribute map.
	 * @return the execution attribute map
	 */
	public MutableAttributeMap getAttributeMap() {
		return attributes;
	}

	/**
	 * Puts a execution attribute into the context.
	 * @param attributeName the attribute name
	 * @param attributeValue the attribute value
	 */
	public void putAttribute(String attributeName, Object attributeValue) {
		attributes.put(attributeName, attributeValue);
	}

}
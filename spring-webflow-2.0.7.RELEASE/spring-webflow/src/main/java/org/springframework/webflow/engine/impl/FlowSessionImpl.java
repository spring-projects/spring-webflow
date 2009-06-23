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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.execution.FlowSession;

/**
 * Implementation of the FlowSession interfaced used internally by the <code>FlowExecutionImpl</code>. This class is
 * closely coupled with <code>FlowExecutionImpl</code> and <code>RequestControlContextImpl</code>. The three
 * classes work together to form a complete flow execution implementation.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
class FlowSessionImpl implements FlowSession, Externalizable {

	private static final String VIEW_SCOPE_ATTRIBUTE = "viewScope";

	/**
	 * The flow definition (a singleton).
	 * <p>
	 * Transient to support restoration by the {@link FlowExecutionImplFactory}.
	 */
	private transient Flow flow;

	/**
	 * The current state of this flow session.
	 * <p>
	 * Transient to support restoration by the {@link FlowExecutionImplFactory}.
	 */
	private transient State state;

	/**
	 * The session data model ("flow scope").
	 */
	private MutableAttributeMap scope = new LocalAttributeMap();

	/**
	 * The parent session of this session (may be <code>null</code> if this is a root session.)
	 */
	private FlowSessionImpl parent;

	/**
	 * Set so the transient {@link #flow} field can be restored by the {@link FlowExecutionImplFactory}.
	 */
	private String flowId;

	/**
	 * Set so the transient {@link #state} field can be restored by the {@link FlowExecutionImplFactory}.
	 */
	private String stateId;

	/**
	 * Default constructor required for externalizable serialization. Should NOT be called programmatically.
	 */
	public FlowSessionImpl() {
	}

	/**
	 * Create a new flow session.
	 * @param flow the flow definition associated with this flow session
	 * @param parent this session's parent (may be null)
	 */
	public FlowSessionImpl(Flow flow, FlowSessionImpl parent) {
		Assert.notNull(flow, "The flow is required");
		this.flow = flow;
		this.parent = parent;
	}

	// implementing FlowSession

	public FlowDefinition getDefinition() {
		return flow;
	}

	public StateDefinition getState() {
		return state;
	}

	public MutableAttributeMap getScope() {
		return scope;
	}

	public MutableAttributeMap getViewScope() throws IllegalStateException {
		if (state == null) {
			throw new IllegalStateException("The current state of this flow '" + flow.getId()
					+ "' is [null] - cannot access view scope");
		}
		if (!state.isViewState()) {
			throw new IllegalStateException("The current state '" + state.getId() + "' of this flow '" + flow.getId()
					+ "' is not a view state - view scope not accessible");
		}
		return (MutableAttributeMap) scope.get(VIEW_SCOPE_ATTRIBUTE);
	}

	public FlowSession getParent() {
		return parent;
	}

	public boolean isRoot() {
		return parent == null;
	}

	// public impl

	public void setCurrentState(State state) {
		if (this.state != null && this.state.isViewState()) {
			destroyViewScope();
		}
		this.state = state;
		if (this.state.isViewState()) {
			initViewScope();
		}
	}

	// custom serialization

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		flowId = (String) in.readObject();
		stateId = (String) in.readObject();
		scope = (MutableAttributeMap) in.readObject();
		parent = (FlowSessionImpl) in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(flow.getId());
		out.writeObject(state != null ? state.getId() : null);
		out.writeObject(scope);
		out.writeObject(parent);
	}

	// package-private

	Flow getFlow() {
		return flow;
	}

	// package private setters used by FlowExecutionImplFactory for setting/updating internal state

	/**
	 * Restores the definition of this flow session.
	 * @param flow the flow sessions definition
	 * @see FlowExecutionImplStateRestorer
	 */
	void setFlow(Flow flow) {
		Assert.notNull(flow, "The flow is required");
		this.flow = flow;
	}

	/**
	 * Set the current state of this flow session.
	 * @param state the state that is currently active in this flow session
	 * @see FlowExecutionImpl#setCurrentState(State)
	 * @see FlowExecutionImplStateRestorer
	 */
	void setState(State state) {
		Assert.notNull(state, "The state is required");
		Assert.isTrue(flow == state.getOwner(),
				"The state does not belong to the flow associated with this flow session");
		this.state = state;
	}

	/**
	 * Returns the de-serialized id indicating the flow id of this session.
	 */
	String getFlowId() {
		if (flow == null) {
			return flowId;
		} else {
			return flow.getId();
		}
	}

	/**
	 * Sets the de-serialized id indicating the flow id of this session. Used for testing only.
	 * @param flowId the flow id
	 */
	void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	/**
	 * Returns the de-serialized id indicating the current state of this session.
	 */
	String getStateId() {
		if (state == null) {
			return stateId;
		} else {
			return state.getId();
		}
	}

	/**
	 * Sets the de-serialized id indicating the state of this session. Used for testing only.
	 * @param stateId the state id
	 */
	void setStateId(String stateId) {
		this.stateId = stateId;
	}

	// internal helpers

	/**
	 * Initialize the view scope data structure.
	 */
	private void initViewScope() {
		scope.put(VIEW_SCOPE_ATTRIBUTE, new LocalAttributeMap());
	}

	/**
	 * Destroy the view scope data structure.
	 */
	private void destroyViewScope() {
		scope.remove(VIEW_SCOPE_ATTRIBUTE);
	}

	public String toString() {
		if (flow != null) {
			return new ToStringCreator(this).append("flow", getFlowId()).append("state", getStateId()).append("scope",
					scope).toString();
		} else {
			return "[Unhydrated session '" + flowId + "' in state '" + stateId + "']";
		}
	}
}
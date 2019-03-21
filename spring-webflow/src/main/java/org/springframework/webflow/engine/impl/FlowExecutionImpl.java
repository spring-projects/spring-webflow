/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.StateManageableMessageContext;
import org.springframework.context.MessageSource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionableState;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;

/**
 * Default implementation of FlowExecution that uses a stack-based data structure to manage spawned flow sessions. This
 * class is closely coupled with package-private <code>FlowSessionImpl</code> and <code>RequestControlContextImpl</code>
 * . The three classes work together to form a complete flow execution implementation based on a finite state machine.
 * <p>
 * This implementation of FlowExecution is serializable so it can be safely stored in an HTTP session or other
 * persistent store such as a file, database, or client-side form field. Once deserialized, the
 * {@link FlowExecutionImplFactory} is expected to be used to restore the execution to a usable state.
 * 
 * @see FlowExecutionImplFactory
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 */
public class FlowExecutionImpl implements FlowExecution, Externalizable {

	private static final Log logger = LogFactory.getLog(FlowExecutionImpl.class);

	private static final String FLASH_SCOPE_ATTRIBUTE = "flashScope";

	/**
	 * The execution's root flow; the top level flow that acts as the starting point for this flow execution.
	 * <p>
	 * Transient to support restoration by the {@link FlowExecutionImplFactory}.
	 */
	private transient Flow flow;

	/**
	 * A enum tracking the status of this flow execution.
	 */
	private FlowExecutionStatus status;

	/**
	 * The stack of active, currently executing flow sessions. As subflows are spawned, they are pushed onto the stack.
	 * As they end, they are popped off the stack.
	 */
	private LinkedList<FlowSessionImpl> flowSessions;

	/**
	 * A thread-safe listener list, holding listeners monitoring the lifecycle of this flow execution.
	 * <p>
	 * Transient to support restoration by the {@link FlowExecutionImplFactory}.
	 */
	private transient FlowExecutionListeners listeners;

	/**
	 * The factory for getting the key to assign this flow execution when needed for persistence.
	 */
	private transient FlowExecutionKeyFactory keyFactory;

	/**
	 * The key assigned to this flow execution. May be null if a key has not been assigned.
	 */
	private transient FlowExecutionKey key;

	/**
	 * A data structure for attributes shared by all flow sessions.
	 * <p>
	 * Transient to support restoration by the {@link FlowExecutionImplFactory}.
	 */
	private transient MutableAttributeMap<Object> conversationScope;

	/**
	 * A data structure for runtime system execution attributes.
	 * <p>
	 * Transient to support restoration by the {@link FlowExecutionImplFactory}.
	 */
	private transient AttributeMap<Object> attributes;

	/**
	 * The outcome reached by this flow execution when it ends.
	 */
	private transient FlowExecutionOutcome outcome;

	/**
	 * Default constructor required for externalizable serialization. Should NOT be called programmatically.
	 */
	public FlowExecutionImpl() {
	}

	/**
	 * Create a new flow execution executing the provided flow. Flow executions are normally created by a flow execution
	 * factory.
	 * @param flow the root flow of this flow execution
	 */
	public FlowExecutionImpl(Flow flow) {
		Assert.notNull(flow, "The flow definition is required");
		this.flow = flow;
		status = FlowExecutionStatus.NOT_STARTED;
		listeners = new FlowExecutionListeners();
		attributes = CollectionUtils.EMPTY_ATTRIBUTE_MAP;
		flowSessions = new LinkedList<>();
		conversationScope = new LocalAttributeMap<>();
		conversationScope.put(FLASH_SCOPE_ATTRIBUTE, new LocalAttributeMap<>());
	}

	public String getCaption() {
		return "execution of '" + flow.getId() + "'";
	}

	// implementing FlowExecutionContext

	public FlowExecutionKey getKey() {
		return key;
	}

	public FlowDefinition getDefinition() {
		return flow;
	}

	public boolean hasStarted() {
		return status == FlowExecutionStatus.ACTIVE || status == FlowExecutionStatus.ENDED;
	}

	public boolean isActive() {
		return status == FlowExecutionStatus.ACTIVE;
	}

	public boolean hasEnded() {
		return status == FlowExecutionStatus.ENDED;
	}

	public FlowExecutionOutcome getOutcome() {
		return outcome;
	}

	public FlowSession getActiveSession() {
		if (!isActive()) {
			if (status == FlowExecutionStatus.NOT_STARTED) {
				throw new IllegalStateException(
						"No active FlowSession to access; this FlowExecution has not been started");
			} else {
				throw new IllegalStateException("No active FlowSession to access; this FlowExecution has ended");
			}
		}
		return getActiveSessionInternal();
	}

	@SuppressWarnings("unchecked")
	public MutableAttributeMap<Object> getFlashScope() {
		return (MutableAttributeMap<Object>) conversationScope.get(FLASH_SCOPE_ATTRIBUTE);
	}

	public MutableAttributeMap<Object> getConversationScope() {
		return conversationScope;
	}

	public AttributeMap<Object> getAttributes() {
		return attributes;
	}

	// methods implementing FlowExecution

	public void start(MutableAttributeMap<?> input, ExternalContext externalContext) throws FlowExecutionException,
			IllegalStateException {
		Assert.state(!hasStarted(), "This flow has already been started; you cannot call 'start()' more than once");
		if (logger.isDebugEnabled()) {
			logger.debug("Starting in " + externalContext + " with input " + input);
		}
		MessageContext messageContext = createMessageContext(null);
		RequestControlContext requestContext = createRequestContext(externalContext, messageContext);
		RequestContextHolder.setRequestContext(requestContext);
		listeners.fireRequestSubmitted(requestContext);
		try {
			start(flow, input, requestContext);
		} catch (FlowExecutionException e) {
			handleException(e, requestContext);
		} catch (Exception e) {
			handleException(wrap(e), requestContext);
		} finally {
			saveFlashMessages(requestContext);
			if (isActive()) {
				try {
					listeners.firePaused(requestContext);
				} catch (Throwable e) {
					logger.error("FlowExecutionListener threw exception", e);
				}
			}
			try {
				listeners.fireRequestProcessed(requestContext);
			} catch (Throwable e) {
				logger.error("FlowExecutionListener threw exception", e);
			}
			RequestContextHolder.setRequestContext(null);
		}
	}

	public void resume(ExternalContext externalContext) throws FlowExecutionException, IllegalStateException {
		Assert.state(status == FlowExecutionStatus.ACTIVE,
				"This FlowExecution cannot be resumed because it is not active; it has either not been started or has ended");
		if (logger.isDebugEnabled()) {
			logger.debug("Resuming in " + externalContext);
		}
		Flow activeFlow = getActiveSessionInternal().getFlow();
		MessageContext messageContext = createMessageContext(activeFlow.getApplicationContext());
		RequestControlContext requestContext = createRequestContext(externalContext, messageContext);
		RequestContextHolder.setRequestContext(requestContext);
		listeners.fireRequestSubmitted(requestContext);
		try {
			listeners.fireResuming(requestContext);
			activeFlow.resume(requestContext);
		} catch (FlowExecutionException e) {
			handleException(e, requestContext);
		} catch (Exception e) {
			handleException(wrap(e), requestContext);
		} finally {
			saveFlashMessages(requestContext);
			if (isActive()) {
				try {
					listeners.firePaused(requestContext);
				} catch (Throwable e) {
					logger.error("FlowExecutionListener threw exception", e);
				}
			}
			try {
				listeners.fireRequestProcessed(requestContext);
			} catch (Throwable e) {
				logger.error("FlowExecutionListener threw exception", e);
			}
			RequestContextHolder.setRequestContext(null);
		}
	}

	/**
	 * Jump to a state of the currently active flow. If this execution has not been started, a new session will be
	 * activated and its current state will be set. This is a implementation-internal method that bypasses the
	 * {@link #start(MutableAttributeMap, ExternalContext)} operation and allows for jumping to an arbitrary flow state.
	 * Useful for testing.
	 * @param stateId the identifier of the state to jump to
	 */
	public void setCurrentState(String stateId) {
		FlowSessionImpl session;
		if (status == FlowExecutionStatus.NOT_STARTED) {
			session = activateSession(flow);
			status = FlowExecutionStatus.ACTIVE;
		} else {
			session = getActiveSessionInternal();
		}
		State state = session.getFlow().getStateInstance(stateId);
		session.setCurrentState(state);
	}

	// custom serialization (implementation of Externalizable for optimized storage)

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		status = (FlowExecutionStatus) in.readObject();
		flowSessions = (LinkedList<FlowSessionImpl>) in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(status);
		out.writeObject(flowSessions);
	}

	public String toString() {
		if (!isActive()) {
			if (!hasStarted()) {
				return "[Not yet started " + getCaption() + "]";
			} else {
				return "[Ended " + getCaption() + "]";
			}
		} else {
			if (flow != null) {
				return new ToStringCreator(this).append("flow", flow.getId()).append("flowSessions", flowSessions)
						.toString();
			} else {
				return "[Unhydrated execution of '" + getRootSession().getFlowId() + "']";
			}
		}
	}

	// subclassing hooks

	/**
	 * Create a flow execution control context.
	 * @param externalContext the external context triggering this request
	 */
	protected RequestControlContext createRequestContext(ExternalContext externalContext, MessageContext messageContext) {
		return new RequestControlContextImpl(this, externalContext, messageContext);
	}

	/**
	 * Create a new flow session object. Subclasses can override this to return a special implementation if required.
	 * @param flow the flow that should be associated with the flow session
	 * @param parent the flow session that should be the parent of the newly created flow session (may be null)
	 * @return the newly created flow session
	 */
	protected FlowSessionImpl createFlowSession(Flow flow, FlowSessionImpl parent) {
		return new FlowSessionImpl(flow, parent);
	}

	// package private request control context callbacks

	void start(Flow flow, MutableAttributeMap<?> input, RequestControlContext context) {
		listeners.fireSessionCreating(context, flow);
		FlowSessionImpl session = activateSession(flow);
		if (session.isRoot()) {
			status = FlowExecutionStatus.ACTIVE;
		}
		if (input == null) {
			input = new LocalAttributeMap<>();
		}
		if (hasEmbeddedModeAttribute(input)) {
			session.setEmbeddedMode();
		}
		StateManageableMessageContext messageContext = (StateManageableMessageContext) context.getMessageContext();
		messageContext.setMessageSource(flow.getApplicationContext());
		listeners.fireSessionStarting(context, session, input);
		flow.start(context, input);
		listeners.fireSessionStarted(context, session);
	}

	void setCurrentState(State newState, RequestContext context) {
		listeners.fireStateEntering(context, newState);
		FlowSessionImpl session = getActiveSessionInternal();
		State previousState = (State) session.getState();
		session.setCurrentState(newState);
		listeners.fireStateEntered(context, previousState);
	}

	public void viewRendering(View view, RequestContext context) {
		listeners.fireViewRendering(context, view);
	}

	public void viewRendered(View view, RequestContext context) {
		listeners.fireViewRendered(context, view);
	}

	boolean handleEvent(Event event, RequestControlContext context) {
		listeners.fireEventSignaled(context, event);
		return getActiveSessionInternal().getFlow().handleEvent(context);
	}

	boolean execute(Transition transition, RequestControlContext context) {
		listeners.fireTransitionExecuting(context, transition);
		return transition.execute((State) getActiveSession().getState(), context);
	}

	void endActiveFlowSession(String outcome, MutableAttributeMap<Object> output, RequestControlContext context) {
		FlowSessionImpl session = getActiveSessionInternal();
		listeners.fireSessionEnding(context, session, outcome, output);
		session.getFlow().end(context, outcome, output);
		flowSessions.removeLast();
		boolean executionEnded = flowSessions.isEmpty();
		if (executionEnded) {
			// set the root flow execution outcome for external clients to use
			this.outcome = new FlowExecutionOutcome(outcome, output);
			status = FlowExecutionStatus.ENDED;
		}
		listeners.fireSessionEnded(context, session, outcome, output);
		if (!executionEnded) {
			// restore any variables that may have transient references
			getActiveSessionInternal().getFlow().restoreVariables(context);
			// treat the outcome as an event against the current state of the new active flow
			context.handleEvent(new Event(session.getState(), outcome, output));
		}
	}

	FlowExecutionKey assignKey() {
		key = keyFactory.getKey(this);
		if (logger.isDebugEnabled()) {
			logger.debug("Assigned key " + key);
		}
		return key;
	}

	void updateCurrentFlowExecutionSnapshot() {
		keyFactory.updateFlowExecutionSnapshot(this);
	}

	void removeCurrentFlowExecutionSnapshot() {
		keyFactory.removeFlowExecutionSnapshot(this);
	}

	void removeAllFlowExecutionSnapshots() {
		keyFactory.removeAllFlowExecutionSnapshots(this);
	}

	TransitionDefinition getMatchingTransition(String eventId) {
		FlowSessionImpl session = getActiveSessionInternal();
		if (session == null) {
			return null;
		}
		TransitionableState currentState = (TransitionableState) session.getState();
		TransitionDefinition transition = currentState.getTransition(eventId);
		if (transition == null) {
			transition = session.getFlow().getGlobalTransition(eventId);
		}
		return transition;
	}

	// package private setters for restoring transient state used by FlowExecutionImplServicesConfigurer

	FlowExecutionListener[] getListeners() {
		return listeners.getArray();
	}

	void setListeners(FlowExecutionListener[] listeners) {
		this.listeners = new FlowExecutionListeners(listeners);
	}

	void setAttributes(AttributeMap<Object> attributes) {
		this.attributes = attributes;
	}

	FlowExecutionKeyFactory getKeyFactory() {
		return keyFactory;
	}

	void setKeyFactory(FlowExecutionKeyFactory keyFactory) {
		this.keyFactory = keyFactory;
	}

	// Used by {@link FlowExecutionImplFactory}

	/**
	 * Returns the list of flow session maintained by this flow execution.
	 */
	LinkedList<FlowSessionImpl> getFlowSessions() {
		return flowSessions;
	}

	/**
	 * Are there any flow sessions in this flow execution?
	 */
	boolean hasSessions() {
		return !flowSessions.isEmpty();
	}

	/**
	 * Are there any sessions for sub flows in this flow execution?
	 */
	boolean hasSubflowSessions() {
		return flowSessions.size() > 1;
	}

	/**
	 * Returns the flow session for the root flow of this flow execution.
	 */
	FlowSessionImpl getRootSession() {
		return flowSessions.getFirst();
	}

	/**
	 * Returns an iterator looping over the subflow sessions in this flow execution.
	 */
	Iterator<FlowSessionImpl> getSubflowSessionIterator() {
		return flowSessions.listIterator(1);
	}

	/**
	 * Restore the flow definition of this flow execution.
	 */
	void setFlow(Flow flow) {
		this.flow = flow;
	}

	/**
	 * Restore conversation scope for this flow execution.
	 */
	void setConversationScope(MutableAttributeMap<Object> conversationScope) {
		this.conversationScope = conversationScope;
	}

	/**
	 * Restore the flow execution key.
	 */
	void setKey(FlowExecutionKey key) {
		this.key = key;
	}

	// internal helpers

	private MessageContext createMessageContext(MessageSource messageSource) {
		StateManageableMessageContext messageContext = new DefaultMessageContext(messageSource);
		Serializable messagesMemento = (Serializable) getFlashScope().extract("messagesMemento");
		if (messagesMemento != null) {
			messageContext.restoreMessages(messagesMemento);
		}
		return messageContext;
	}

	/**
	 * Activate a new <code>FlowSession</code> for the flow definition. Creates the new flow session and pushes it onto
	 * the stack.
	 * @param flow the flow definition
	 * @return the new flow session
	 */
	private FlowSessionImpl activateSession(Flow flow) {
		FlowSessionImpl parent = getActiveSessionInternal();
		FlowSessionImpl session = createFlowSession(flow, parent);
		flowSessions.add(session);
		return session;
	}

	private FlowSessionImpl getActiveSessionInternal() {
		if (flowSessions.isEmpty()) {
			return null;
		}
		return flowSessions.getLast();
	}

	private void saveFlashMessages(RequestContext context) {
		StateManageableMessageContext messageContext = (StateManageableMessageContext) context.getMessageContext();
		Serializable messagesMemento = messageContext.createMessagesMemento();
		getFlashScope().put("messagesMemento", messagesMemento);
	}

	private FlowExecutionException wrap(Exception e) {
		if (isActive()) {
			FlowSession session = getActiveSession();
			String flowId = session.getDefinition().getId();
			String stateId = session.getState() != null ? session.getState().getId() : null;
			return new FlowExecutionException(flowId, stateId, "Exception thrown in state '" + stateId + "' of flow '"
					+ flowId + "'", e);
		} else {
			return new FlowExecutionException(flow.getId(), null, "Exception thrown within inactive flow '"
					+ flow.getId() + "'", e);
		}
	}

	/**
	 * Handles an exception that occurred performing an operation on this flow execution. First tries the set of
	 * exception handlers associated with the offending state, then the handlers at the flow level.
	 * @param exception the exception that occurred
	 * @param context the request control context the exception occurred in
	 * @throws FlowExecutionException re-throws the exception if it was not handled at the state or flow level
	 */
	private void handleException(FlowExecutionException exception, RequestControlContext context) {
		listeners.fireExceptionThrown(context, exception);
		if (logger.isDebugEnabled()) {
			if (exception.getCause() != null) {
				logger.debug("Attempting to handle [" + exception + "] with root cause [" + getRootCause(exception)
						+ "]");
			} else {
				logger.debug("Attempting to handle [" + exception + "]");
			}
		}
		if (!isActive()) {
			throw exception;
		}
		boolean handled = false;
		try {
			if (tryStateHandlers(exception, context) || tryFlowHandlers(exception, context)) {
				handled = true;
			}
		} catch (FlowExecutionException newException) {
			// exception handling itself resulted in a new FlowExecutionException, try to handle it
			handleException(newException, context);
			handled = true;
		}
		if (!handled) {
			if (logger.isDebugEnabled()) {
				logger.debug("Rethrowing unhandled flow execution exception");
			}
			throw exception;
		}
	}

	/**
	 * Get the root cause of the given throwable.
	 */
	private Throwable getRootCause(Throwable e) {
		Throwable cause = e.getCause();
		return cause == null ? e : getRootCause(cause);
	}

	/**
	 * Try to handle given exception using execution exception handlers registered at the state level. Returns null if
	 * no handler handled the exception.
	 * @return true if the exception was handled
	 */
	private boolean tryStateHandlers(FlowExecutionException exception, RequestControlContext context) {
		if (exception.getStateId() != null) {
			State state = getActiveSessionInternal().getFlow().getStateInstance(exception.getStateId());
			return state.handleException(exception, context);
		} else {
			return false;
		}
	}

	/**
	 * Try to handle given exception using execution exception handlers registered at the flow level. Returns null if no
	 * handler handled the exception.
	 * @return true if the exception was handled
	 */
	private boolean tryFlowHandlers(FlowExecutionException exception, RequestControlContext context) {
		return getActiveSessionInternal().getFlow().handleException(exception, context);
	}

	private boolean hasEmbeddedModeAttribute(AttributeMap<?> input) {
		if (input != null) {
			String mode = (String) input.get("mode");
			if (mode != null && mode.trim().toLowerCase().equals("embedded")) {
				return true;
			}
		}
		return false;
	}

}

/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.execution.repository.continuation;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionRestorationFailureException;
import org.springframework.webflow.execution.repository.support.AbstractConversationFlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.FlowExecutionStateRestorer;
import org.springframework.webflow.util.RandomGuidUidGenerator;
import org.springframework.webflow.util.UidGenerator;

/**
 * Stores <i>one to many</i> flow execution continuations (snapshots) per
 * conversation, where each continuation represents a paused, restorable
 * view-state of a flow execution snapshotted at a point in time.
 * <p>
 * The set of active user conversations are managed by a
 * {@link ConversationManager} implementation, which this repository delegates
 * to.
 * <p>
 * This repository is responsible for:
 * <ul>
 * <li>Beginning a new conversation when a new flow execution is made
 * persistent. Each conversation is assigned a unique conversartion id which
 * forms one part of the flow execution key.
 * <li>Associating a flow execution with that conversation by adding a
 * {@link FlowExecutionContinuation} to a continuation group.<br>
 * When a flow execution is placed in this repository a new continuation
 * snapshot is created, assigned an id, and added to the group. Each
 * continuation logically represents a state of the conversation at a point in
 * time <i>that can be restored and continued</i>. These continuations can be
 * restored to support users going back in their browser to continue a
 * conversation from a previous point.
 * <li>Ending existing conversations when persistent flow executions end, as
 * part of a repository removal operation.
 * </ul>
 * <p>
 * This repository implementation also provides support for <i>conversation
 * invalidation after completion</i>, where once a logical conversation
 * completes (by one of its FlowExecution's reaching an end state), the entire
 * conversation (including all continuations) is invalidated. This prevents the
 * possibility of duplicate submission after completion.
 * <p>
 * This repository implementation should be considered when you do have to
 * support browser navigational button use, e.g. you cannot lock down the
 * browser and require that all navigational events to be routed explicitly
 * through Spring Web Flow.
 * 
 * @author Keith Donald
 */
public class ContinuationFlowExecutionRepository extends AbstractConversationFlowExecutionRepository {

	/**
	 * The conversation attribute that stores the "continuation group".
	 */
	private static final String CONTINUATION_GROUP_ATTRIBUTE = "continuationGroup";

	/**
	 * The continuation factory that will be used to create new continuations to
	 * be added to active conversations.
	 */
	private FlowExecutionContinuationFactory continuationFactory = new SerializedFlowExecutionContinuationFactory();

	/**
	 * The uid generation strategy to use.
	 */
	private UidGenerator continuationIdGenerator = new RandomGuidUidGenerator();

	/**
	 * The maximum number of continuations that can be active per conversation.
	 * The default is 30, which is high enough not to interfere with the user experience
	 * of normal users using the back button, but low enough to avoid excessive
	 * resource usage or easy denial of service attacks.
	 */
	private int maxContinuations = 30;

	/**
	 * Create a new continuation based flow execution repository using given state
	 * restorer and conversation manager.
	 * @param executionStateRestorer the state restoration strategy to use
	 * @param conversationManager the conversation manager to use
	 */
	public ContinuationFlowExecutionRepository(FlowExecutionStateRestorer executionStateRestorer,
			ConversationManager conversationManager) {
		super(executionStateRestorer, conversationManager);
	}

	/**
	 * Returns the continuation factory that encapsulates the construction of
	 * continuations stored in this repository. Defaults to
	 * {@link SerializedFlowExecutionContinuationFactory}.
	 */
	public FlowExecutionContinuationFactory getContinuationFactory() {
		return continuationFactory;
	}

	/**
	 * Sets the continuation factory that encapsulates the construction of
	 * continuations stored in this repository.
	 */
	public void setContinuationFactory(FlowExecutionContinuationFactory continuationFactory) {
		Assert.notNull(continuationFactory, "The continuation factory is required");
		this.continuationFactory = continuationFactory;
	}

	/**
	 * Returns the uid generation strategy used to generate continuation
	 * identifiers. Defaults to {@link RandomGuidUidGenerator}.
	 */
	public UidGenerator getContinuationIdGenerator() {
		return continuationIdGenerator;
	}

	/**
	 * Sets the uid generation strategy used to generate unique continuation
	 * identifiers for {@link FlowExecutionKey flow execution keys}.
	 */
	public void setContinuationIdGenerator(UidGenerator continuationIdGenerator) {
		Assert.notNull(continuationIdGenerator, "The continuation id generator is required");
		this.continuationIdGenerator = continuationIdGenerator;
	}

	/**
	 * Returns the maximum number of continuations allowed per conversation in
	 * this repository.
	 */
	public int getMaxContinuations() {
		return maxContinuations;
	}

	/**
	 * Sets the maximum number of continuations allowed per conversation in this
	 * repository. Use -1 for unlimited. The default is 30.
	 */
	public void setMaxContinuations(int maxContinuations) {
		this.maxContinuations = maxContinuations;
	}
	
	public FlowExecution getFlowExecution(FlowExecutionKey key) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting flow execution with key '" + key + "'");
		}
		FlowExecutionContinuation continuation = getContinuation(key);
		try {
			FlowExecution execution = continuation.unmarshal();
			// flow execution was deserialized, so restore transient state
			return getExecutionStateRestorer().restoreState(execution, getConversationScope(key));
		}
		catch (ContinuationUnmarshalException e) {
			throw new FlowExecutionRestorationFailureException(key, e);
		}
	}

	public void putFlowExecution(FlowExecutionKey key, FlowExecution flowExecution) {
		if (logger.isDebugEnabled()) {
			logger.debug("Putting flow execution '" + flowExecution + "' into repository with key '" + key + "'");
		}
		FlowExecutionContinuationGroup continuationGroup = getContinuationGroup(key);
		FlowExecutionContinuation continuation = continuationFactory.createContinuation(flowExecution);
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new continuation to group with id " + getContinuationId(key));
		}		
		continuationGroup.add(getContinuationId(key), continuation);
		putConversationScope(key, flowExecution.getConversationScope());
	}

	protected void onBegin(Conversation conversation) {
		// setup a new continuation group for the conversation
		FlowExecutionContinuationGroup continuationGroup = new FlowExecutionContinuationGroup(maxContinuations);
		conversation.putAttribute(CONTINUATION_GROUP_ATTRIBUTE, continuationGroup);
	}

	protected Serializable generateContinuationId(FlowExecution flowExecution) {
		return continuationIdGenerator.generateUid();
	}

	protected Serializable parseContinuationId(String encodedId) {
		return continuationIdGenerator.parseUid(encodedId);
	}

	/**
	 * Returns the continuation group associated with the governing
	 * conversation.
	 * @param key the flow execution key
	 * @return the continuation group
	 */
	FlowExecutionContinuationGroup getContinuationGroup(FlowExecutionKey key) {
		FlowExecutionContinuationGroup group =
			(FlowExecutionContinuationGroup)getConversation(key).getAttribute(CONTINUATION_GROUP_ATTRIBUTE);
		return group;
	}

	/**
	 * Returns the continuation in the group with the specified key.
	 * @param key the flow execution key
	 * @return the continuation.
	 */
	protected FlowExecutionContinuation getContinuation(FlowExecutionKey key)
			throws FlowExecutionRestorationFailureException {
		try {
			return getContinuationGroup(key).get(getContinuationId(key));
		}
		catch (ContinuationNotFoundException e) {
			throw new FlowExecutionRestorationFailureException(key, e);
		}
	}
}
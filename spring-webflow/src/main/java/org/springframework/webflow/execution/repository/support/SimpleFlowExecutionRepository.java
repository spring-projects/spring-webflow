/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.execution.repository.support;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.PermissionDeniedFlowExecutionAccessException;
import org.springframework.webflow.util.RandomGuidUidGenerator;
import org.springframework.webflow.util.UidGenerator;

/**
 * Conversation manager based flow execution repository that stores
 * <i>exactly one</i> flow execution per conversation.
 * <p>
 * It is important to note that by default use of this repository <b>does not</b>
 * allow for duplicate submission in conjunction with browser navigational buttons
 * (such as the back button). Specifically, if you attempt to "go back" and resubmit,
 * the continuation id stored on the page in your browser history will <b>not</b>
 * match the continuation id of the flow execution entry and access to the
 * conversation will be disallowed. This is because the
 * <code>continuationId</code> changes on each request to consistently prevent
 * the possibility of duplicate submission ({@link #setAlwaysGenerateNewNextKey(boolean)}).
 * <p>
 * This repository is specifically designed to be 'simple': incurring minimal
 * resources and overhead, as only one {@link FlowExecution} is stored <i>per
 * conversation</i>. This repository implementation should only be used
 * when you do not have to support browser navigational button use, e.g. you
 * lock down the browser and require that all navigational events to be routed
 * explicitly through Spring Web Flow.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public class SimpleFlowExecutionRepository extends AbstractConversationFlowExecutionRepository {

	/**
	 * The conversation attribute holding the flow execution entry.
	 */
	private static final String FLOW_EXECUTION_ENTRY_ATTRIBUTE = "flowExecutionEntry";

	/**
	 * Flag to indicate whether or not a new flow execution key should always be
	 * generated before each put call. Default is true.
	 */
	private boolean alwaysGenerateNewNextKey = true;
	
	/**
	 * The uid generation strategy to use.
	 */
	private UidGenerator continuationIdGenerator = new RandomGuidUidGenerator();

	/**
	 * Create a new simple repository using given state restorer and conversation manager.
	 * @param executionStateRestorer the flow execution state restoration strategy to use
	 * @param conversationManager the conversation manager to use
	 */
	public SimpleFlowExecutionRepository(FlowExecutionStateRestorer executionStateRestorer,
			ConversationManager conversationManager) {
		super(executionStateRestorer, conversationManager);
	}

	/**
	 * Returns whether or not a new flow execution key should always be
	 * generated before each put call. Default is true.
	 */
	public boolean isAlwaysGenerateNewNextKey() {
		return alwaysGenerateNewNextKey;
	}

	/**
	 * Sets a flag indicating if a new {@link FlowExecutionKey} should always be
	 * generated before each put call. By setting this to false a FlowExecution
	 * can remain identified by the same key throughout its life.
	 */
	public void setAlwaysGenerateNewNextKey(boolean alwaysGenerateNewNextKey) {
		this.alwaysGenerateNewNextKey = alwaysGenerateNewNextKey;
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

	public FlowExecutionKey getNextKey(FlowExecution flowExecution, FlowExecutionKey previousKey) {
		if (isAlwaysGenerateNewNextKey()) {
			return super.getNextKey(flowExecution, previousKey);
		}
		else {
			return previousKey;
		}
	}

	public FlowExecution getFlowExecution(FlowExecutionKey key) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting flow execution with key '" + key + "'");
		}

		try {
			FlowExecution execution = getEntry(key).access(getContinuationId(key));
			// it could be that the entry was serialized out and read back in, so
			// we need to restore transient flow execution state
			return getExecutionStateRestorer().restoreState(execution, getConversationScope(key));
		}
		catch (InvalidContinuationIdException e) {
			throw new PermissionDeniedFlowExecutionAccessException(key, e);
		}
	}

	public void putFlowExecution(FlowExecutionKey key, FlowExecution flowExecution) {
		if (logger.isDebugEnabled()) {
			logger.debug("Putting flow execution '" + flowExecution + "' into repository with key '" + key + "'");
		}

		FlowExecutionEntry entry = new FlowExecutionEntry(getContinuationId(key), flowExecution);
		putEntry(key, entry);
		putConversationScope(key, flowExecution.getConversationScope());
	}

	protected Serializable generateContinuationId(FlowExecution flowExecution) {
		return continuationIdGenerator.generateUid();
	}

	protected Serializable parseContinuationId(String encodedId) {
		return continuationIdGenerator.parseUid(encodedId);
	}
	
	// internal helpers

	/**
	 * Lookup the entry for keyed flow execution in the governing conversation.
	 */
	private FlowExecutionEntry getEntry(FlowExecutionKey key) {
		FlowExecutionEntry entry =
			(FlowExecutionEntry)getConversation(key).getAttribute(FLOW_EXECUTION_ENTRY_ATTRIBUTE);
		if (entry == null) {
			throw new IllegalStateException("No '" + FLOW_EXECUTION_ENTRY_ATTRIBUTE
					+ "' attribute present in the governing conversation: "
					+ "possible programmer error -- do not call get before calling put");
		}
		return entry;
	}

	/**
	 * Store given flow execution entry in the governing conversation using given key.
	 * @param key the key to use
	 * @param entry the entry to store
	 */
	private void putEntry(FlowExecutionKey key, FlowExecutionEntry entry) {
		getConversation(key).putAttribute(FLOW_EXECUTION_ENTRY_ATTRIBUTE, entry);
	}

	/**
	 * Simple holder for a flow execution. In order to access the held flow
	 * execution you must present a valid continuationId.
	 * 
	 * @author Keith Donald
	 */
	private static class FlowExecutionEntry implements Serializable {

		/**
		 * The id required to access the execution.
		 */
		private Serializable continuationId;

		/**
		 * The flow execution.
		 */
		private FlowExecution flowExecution;

		/**
		 * Creates a new flow execution entry.
		 * @param continuationId the continuation id
		 * @param flowExecution the flow execution
		 */
		public FlowExecutionEntry(Serializable continuationId, FlowExecution flowExecution) {
			this.continuationId = continuationId;
			this.flowExecution = flowExecution;
		}

		/**
		 * Access the wrapped flow execution, using given continuation id as a <i>password</i>.
		 * @param continuationId the continuation id to match
		 * @return the flow execution
		 * @throws InvalidContinuationIdException given continuation id does not match the
		 * continuation id stored in this entry
		 */
		public FlowExecution access(Serializable continuationId) throws InvalidContinuationIdException {
			if (!this.continuationId.equals(continuationId)) {
				throw new InvalidContinuationIdException(continuationId);
			}
			return flowExecution;
		}
	}
}
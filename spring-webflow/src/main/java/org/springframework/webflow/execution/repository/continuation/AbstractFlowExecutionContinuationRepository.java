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
package org.springframework.webflow.execution.repository.continuation;

import org.springframework.util.Assert;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.repository.support.AbstractFlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.FlowExecutionStateRestorer;

/**
 * Base class for repositories that create flow execution snapshots called "continuations".
 * 
 * @author Keith Donald
 */
public abstract class AbstractFlowExecutionContinuationRepository extends AbstractFlowExecutionRepository {

	/**
	 * The continuation factory that will be used to create new continuations to be added to active conversations.
	 */
	private FlowExecutionContinuationFactory continuationFactory;

	/**
	 * Creates a new continuation repository.
	 * @param conversationManager the conversation manager
	 * @param executionStateRestorer the execution state restorer
	 * @param continuationFactory the continuation factory
	 */
	public AbstractFlowExecutionContinuationRepository(ConversationManager conversationManager,
			FlowExecutionStateRestorer executionStateRestorer, FlowExecutionContinuationFactory continuationFactory) {
		super(conversationManager, executionStateRestorer);
		Assert.notNull(continuationFactory, "The flow execution continuation factory is required");
		this.continuationFactory = continuationFactory;
	}

	/**
	 * Take a new continuation snapshot.
	 * @param flowExecution the execution to snapshot
	 * @return the continuation snapshot
	 */
	protected FlowExecutionContinuation snapshot(FlowExecution flowExecution) {
		return continuationFactory.createContinuation(flowExecution);
	}

	/**
	 * Deserialize a serialized flow execution.
	 * @param continuationBytes the flow execution snapshot byte array
	 * @return the deserialized flow execution
	 */
	protected FlowExecution deserializeExecution(byte[] continuationBytes) {
		return continuationFactory.restoreContinuation(continuationBytes).unmarshal();
	}
}
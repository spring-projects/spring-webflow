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
package org.springframework.webflow.execution.repository.impl;

import java.io.Serializable;

import org.springframework.webflow.conversation.Conversation;
import org.springframework.webflow.conversation.ConversationException;
import org.springframework.webflow.conversation.ConversationId;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.ConversationParameters;
import org.springframework.webflow.conversation.NoSuchConversationException;
import org.springframework.webflow.core.collection.CollectionUtils;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionRestorationFailureException;
import org.springframework.webflow.execution.repository.continuation.AbstractFlowExecutionContinuationRepository;
import org.springframework.webflow.execution.repository.continuation.ContinuationUnmarshalException;
import org.springframework.webflow.execution.repository.continuation.FlowExecutionContinuation;
import org.springframework.webflow.execution.repository.continuation.SerializedFlowExecutionContinuationFactory;
import org.springframework.webflow.execution.repository.support.FlowExecutionStateRestorer;
import org.springframework.webflow.util.Base64;

/**
 * Stores flow execution state client side, requiring no use of server-side state.
 * <p>
 * More specifically, instead of putting {@link FlowExecution} objects in a server-side store this repository <i>encodes</i>
 * them directly into the <code>continuationId</code> of the generated {@link FlowExecutionKey}. When asked to load a
 * flow execution by its key this repository decodes the serialized <code>continuationId</code>, restoring the
 * {@link FlowExecution} object at the state it was in when encoded.
 * <p>
 * Note: currently this repository implementation does not by default support <i>conversation management</i>. This has
 * two consequences. First, there is no <i>conversation invalidation after completion</i>, which enables automatic
 * prevention of duplicate submission after a conversation has completed. Secondly, The contents of <i>conversation
 * scope</i> will not be maintained across requests. Support for these features requires tracking active conversations
 * using a conversation service backed by some centralized storage medium like a database table. If you want to have
 * proper conversation management, configure this class with an appropriate conversation manager (the default
 * conversation manager used does nothing).
 * <p>
 * Warning: storing state (a flow execution continuation) on the client entails a certain security risk. This
 * implementation does not provide a secure way of storing state on the client, so a malicious client could reverse
 * engineer a continuation and get access to possible sensitive data stored in the flow execution. If you need more
 * security and still want to store continuations on the client, subclass this class and override the methods
 * {@link #encode(FlowExecution)} and {@link #decode(Serializable)}, implementing them with a secure encoding/decoding
 * algorithm, e.g. based on public/private key encryption.
 * 
 * @see Base64
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class ClientFlowExecutionRepository extends AbstractFlowExecutionContinuationRepository {

	/**
	 * Creates a new client continuation repository. Uses a 'no op' conversation manager by default.
	 * @param executionStateRestorer the transient flow execution state restorer
	 */
	public ClientFlowExecutionRepository(FlowExecutionStateRestorer executionStateRestorer) {
		this(new NoOpConversationManager(), executionStateRestorer);
	}

	/**
	 * Creates a new client continuation repository. Use this constructor when you want to use a particular conversation
	 * manager, e.g. one that does proper conversation management.
	 * @param conversationManager the conversation manager for managing centralized conversational state
	 * @param executionStateRestorer the transient flow execution state restorer
	 */
	public ClientFlowExecutionRepository(ConversationManager conversationManager,
			FlowExecutionStateRestorer executionStateRestorer) {
		super(conversationManager, executionStateRestorer, new SerializedFlowExecutionContinuationFactory());
	}

	public FlowExecution getFlowExecution(FlowExecutionKey key) {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting flow execution with key '" + key + "'");
		}
		// note that the call to getConversationScope() below will try to obtain
		// the conversation identified by the key, which will fail if that conversation
		// is no longer managed by the conversation manager (i.e. it has expired)
		try {
			Serializable encodedExecution = getContinuationId(key);
			FlowExecution execution = decode(encodedExecution);
			return restoreTransientState(execution, execution.getKey());
		} catch (ContinuationUnmarshalException e) {
			throw new FlowExecutionRestorationFailureException(key, e);
		}
	}

	public void putFlowExecution(FlowExecution flowExecution) {
		// search for key in response, replace key value with execution state value
		// call #encode(FlowExecution) to get state value
		putConversationScope(flowExecution);
	}

	/**
	 * Encode given flow execution object into data that can be stored on the client.
	 * <p>
	 * Subclasses can override this to change the encoding algorithm. This class just does a BASE64 encoding of the
	 * serialized flow execution.
	 * @param flowExecution the flow execution instance
	 * @return the encoded representation
	 */
	protected Serializable encode(FlowExecution flowExecution) {
		FlowExecutionContinuation continuation = snapshot(flowExecution);
		return new Base64(true).encodeToString(continuation.toByteArray());
	}

	/**
	 * Decode given data, received from the client, and return the corresponding flow execution object.
	 * <p>
	 * Subclasses can override this to change the decoding algorithm. This class just does a <code>BASE64</code>
	 * decoding and then deserializes the flow execution.
	 * @param encodedExecution the encoded flow execution data
	 * @return the decoded flow execution instance
	 */
	protected FlowExecution decode(Serializable encodedExecution) {
		byte[] continuationBytes = new Base64(true).decodeFromString((String) encodedExecution);
		return deserializeExecution(continuationBytes);
	}

	/**
	 * Conversation manager that doesn't do anything - the default. Does not support conversation scope or conversation
	 * invalidation.
	 * 
	 * @author Keith Donald
	 */
	private static class NoOpConversationManager implements ConversationManager {

		/**
		 * The single conversation managed by the manager.
		 */
		private static final NoOpConversation INSTANCE = new NoOpConversation();

		public Conversation beginConversation(ConversationParameters conversationParameters)
				throws ConversationException {
			return INSTANCE;
		}

		public Conversation getConversation(ConversationId id) throws NoSuchConversationException {
			return INSTANCE;
		}

		public ConversationId parseConversationId(String encodedId) throws ConversationException {
			return NoOpConversation.ID;
		}

		private static class NoOpConversation implements Conversation {

			private static final ConversationId ID = new ConversationId() {
				public String toString() {
					return "NoOpConversation id";
				}
			};

			public ConversationId getId() {
				return ID;
			}

			public void lock() {
			}

			public Object getAttribute(Object name) {
				return CollectionUtils.EMPTY_ATTRIBUTE_MAP;
			}

			public void putAttribute(Object name, Object value) {
			}

			public void removeAttribute(Object name) {
			}

			public void end() {
			}

			public void unlock() {
			}
		}
	}
}
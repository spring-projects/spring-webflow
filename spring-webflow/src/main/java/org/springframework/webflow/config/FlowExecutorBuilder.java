/*
 * Copyright 2004-2014 the original author or authors.
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
package org.springframework.webflow.config;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.ConditionalFlowExecutionListenerLoader;
import org.springframework.webflow.execution.factory.FlowExecutionListenerCriteriaFactory;
import org.springframework.webflow.execution.repository.impl.DefaultFlowExecutionRepository;
import org.springframework.webflow.execution.repository.snapshot.FlowExecutionSnapshotFactory;
import org.springframework.webflow.execution.repository.snapshot.SerializedFlowExecutionSnapshotFactory;
import org.springframework.webflow.execution.repository.snapshot.SimpleFlowExecutionSnapshotFactory;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * A builder for {@link FlowExecutor} instances designed for programmatic use in
 * {@code @Bean} factory methods. For XML configuration consider using the
 * {@code webflow-config} XML namespace.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class FlowExecutorBuilder {

	private final FlowDefinitionLocator flowRegistry;

	private Integer maxFlowExecutions;

	private Integer maxFlowExecutionSnapshots;

	private LocalAttributeMap<Object> executionAttributes = new LocalAttributeMap<Object>();

	private ConditionalFlowExecutionListenerLoader listenerLoader;

	private FlowExecutionListenerCriteriaFactory listenerCriteriaFactory = new FlowExecutionListenerCriteriaFactory();

	private ConversationManager conversationManager;


	public FlowExecutorBuilder(FlowDefinitionLocator flowRegistry) {
		Assert.notNull(flowRegistry, "FlowDefinitionLocator is required");
		this.flowRegistry = flowRegistry;
	}

	/**
	 * Create a new instance with the given flow registry and ApplicationContext.
	 *
	 * @param flowRegistry the flow registry that will locate flow definitions
	 * @param applicationContext the Spring ApplicationContext
	 * @deprecated as of 2.5 an ApplicationContext is no longer required
	 */
	public FlowExecutorBuilder(FlowDefinitionLocator flowRegistry, ApplicationContext applicationContext) {
		Assert.notNull(flowRegistry, "FlowDefinitionLocator is required");
		this.flowRegistry = flowRegistry;
	}


	/**
	 * Set the maximum number of allowed flow executions per user.
	 * @param maxFlowExecutions the max flow executions
	 */
	public FlowExecutorBuilder setMaxFlowExecutions(int maxFlowExecutions) {
		this.maxFlowExecutions = maxFlowExecutions;
		return this;
	}

	/**
	 * Set the maximum number of history snapshots allowed per flow execution.
	 * @param maxFlowExecutionSnapshots the max flow execution snapshots
	 */
	public FlowExecutorBuilder setMaxFlowExecutionSnapshots(int maxFlowExecutionSnapshots) {
		this.maxFlowExecutionSnapshots = maxFlowExecutionSnapshots;
		return this;
	}

	/**
	 * Whether flow executions should redirect after they pause before rendering.
	 * @param redirectOnPause whether to redirect or not
	 */
	public FlowExecutorBuilder setAlwaysRedirectOnPause(boolean redirectOnPause) {
		this.executionAttributes.put("alwaysRedirectOnPause", redirectOnPause);
		return this;
	}

	/**
	 * Whether flow executions redirect after they pause for transitions that remain
	 * in the same view state. This attribute effectively overrides the value of the
	 * "always-redirect-on-pause" attribute in same state transitions.
	 * @param redirectInSameState whether to redirect or not
	 */
	public FlowExecutorBuilder setRedirectInSameState(boolean redirectInSameState) {
		this.executionAttributes.put("redirectInSameState", redirectInSameState);
		return this;
	}

	/**
	 * Add a single flow execution meta attribute.
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public FlowExecutorBuilder addFlowExecutionAttribute(String name, Object value) {
		this.executionAttributes.put(name, value);
		return this;
	}

	/**
	 * Register a {@link FlowExecutionListener} that observes the lifecycle of all flow
	 * executions launched by this executor.
	 * @param listener the listener to be registered
	 */
	public FlowExecutorBuilder addFlowExecutionListener(FlowExecutionListener listener) {
		return addFlowExecutionListener(listener, "*");
	}

	/**
	 * Register a {@link FlowExecutionListener} that observes the lifecycle of flow
	 * executions launched by this executor.
	 * @param listener the listener to be registered
	 * @param criteria the criteria that determines the flow definitions a listener
	 * 	should observe, delimited by commas or '*' for "all".
	 * 	Example: 'flow1,flow2,flow3'.
	 */
	public FlowExecutorBuilder addFlowExecutionListener(FlowExecutionListener listener, String criteria) {
		if (this.listenerLoader == null) {
			this.listenerLoader = new ConditionalFlowExecutionListenerLoader();
		}
		this.listenerLoader.addListener(listener, this.listenerCriteriaFactory.getListenerCriteria(criteria));
		return this;
	}

	/**
	 * Set the ConversationManager implementation to use for storing conversations
	 * in the session effectively controlling how state is stored physically when
	 * a flow execution is paused.. Note that when this attribute is provided, the
	 * "max-execution-snapshots" attribute is meaningless.
	 * @param conversationManager the ConversationManager instance to use
	 */
	public FlowExecutorBuilder setConversationManager(ConversationManager conversationManager) {
		this.conversationManager = conversationManager;
		return this;
	}

	/**
	 * Create and return a {@link FlowExecutor} instance.
	 */
	public FlowExecutor build() {
		FlowExecutionImplFactory executionFactory = getExecutionFactory();
		DefaultFlowExecutionRepository executionRepository = getFlowExecutionRepository(executionFactory);
		executionFactory.setExecutionKeyFactory(executionRepository);
		return new FlowExecutorImpl(this.flowRegistry, executionFactory, executionRepository);
	}


	private FlowExecutionImplFactory getExecutionFactory() {
		FlowExecutionImplFactory executionFactory = new FlowExecutionImplFactory();
		executionFactory.setExecutionAttributes(getExecutionAttributes());
		if (this.listenerLoader != null) {
			executionFactory.setExecutionListenerLoader(this.listenerLoader);
		}
		return executionFactory;
	}

	private DefaultFlowExecutionRepository getFlowExecutionRepository(FlowExecutionFactory executionFactory) {
		ConversationManager manager = getConversationManager();
		FlowExecutionSnapshotFactory snapshotFactory = getSnapshotFactory(executionFactory);
		DefaultFlowExecutionRepository repository = new DefaultFlowExecutionRepository(manager, snapshotFactory);
		if (this.maxFlowExecutionSnapshots != null) {
			repository.setMaxSnapshots((this.maxFlowExecutionSnapshots == 0) ? 1 : this.maxFlowExecutionSnapshots);
		}
		return repository;
	}

	private ConversationManager getConversationManager() {
		ConversationManager manager = this.conversationManager;
		if (manager == null) {
			manager = new SessionBindingConversationManager();
		}
		if (this.maxFlowExecutions != null && manager instanceof SessionBindingConversationManager) {
			((SessionBindingConversationManager) manager).setMaxConversations(this.maxFlowExecutions);
		}
		return manager;
	}

	private FlowExecutionSnapshotFactory getSnapshotFactory(FlowExecutionFactory executionFactory) {
		FlowExecutionSnapshotFactory factory = null;
		if (this.maxFlowExecutionSnapshots != null && this.maxFlowExecutionSnapshots == 0) {
			factory = new SimpleFlowExecutionSnapshotFactory(executionFactory, this.flowRegistry);
		}
		else {
			factory = new SerializedFlowExecutionSnapshotFactory(executionFactory, this.flowRegistry);
		}
		return factory;
	}

	private LocalAttributeMap<Object> getExecutionAttributes() {
		LocalAttributeMap<Object> attributes = new LocalAttributeMap<Object>(this.executionAttributes.asMap());
		if (!attributes.contains("alwaysRedirectOnPause")) {
			attributes.put("alwaysRedirectOnPause", true);
		}
		if (!attributes.contains("redirectInSameState")) {
			attributes.put("redirectInSameState", true);
		}
		return attributes;
	}

}

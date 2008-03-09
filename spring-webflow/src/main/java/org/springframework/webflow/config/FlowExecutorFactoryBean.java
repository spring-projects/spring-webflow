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
package org.springframework.webflow.config;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.util.Assert;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.impl.FlowExecutionImplStateRestorer;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKeyFactory;
import org.springframework.webflow.execution.factory.FlowExecutionListenerLoader;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.repository.impl.DefaultFlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.FlowExecutionStateRestorer;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * The default flow executor factory implementation. As a <code>FactoryBean</code>, this class has been designed for
 * use as a Spring managed bean.
 * <p>
 * This factory encapsulates the construction and assembly of a {@link FlowExecutor}, including the provision of its
 * {@link FlowExecutionRepository} strategy.
 * <p>
 * The definition locator property is required, all other properties are optional.
 * <p>
 * This class has been designed with subclassing in mind. If you want to do advanced Spring Web Flow customization, e.g.
 * using a custom {@link org.springframework.webflow.executor.FlowExecutor} implementation, consider subclassing this
 * class and overriding one or more of the provided hook methods.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
class FlowExecutorFactoryBean implements FactoryBean, InitializingBean {

	/**
	 * The locator the executor will use to access flow definitions registered in a central registry. Required.
	 */
	private FlowDefinitionLocator flowDefinitionLocator;

	/**
	 * Execution attributes to apply.
	 */
	private Set flowExecutionAttributes;

	/**
	 * The loader that will determine which listeners to attach to flow definition executions.
	 */
	private FlowExecutionListenerLoader flowExecutionListenerLoader;

	/**
	 * The conversation manager to be used by the flow execution repository to store state associated with conversations
	 * driven by Spring Web Flow.
	 */
	private ConversationManager conversationManager;

	/**
	 * The maximum number of allowed concurrent conversations in the session.
	 */
	private Integer maxConversations;

	/**
	 * The type of execution repository to configure with executors created by this factory. Optional. Will fallback to
	 * default value if not set.
	 */
	private FlowExecutionRepositoryType flowExecutionRepositoryType;

	/**
	 * The maximum number of allowed continuations for a single conversation. Only used when the repository type is
	 * {@link FlowExecutionRepositoryType#CONTINUATION}.
	 */
	private Integer maxContinuations;

	/**
	 * The conversion service to use for type conversion of flow execution attribute values.
	 */
	private ConversionService conversionService = new DefaultConversionService();

	/**
	 * The flow executor this factory bean creates.
	 */
	private FlowExecutor flowExecutor;

	/**
	 * Sets the flow definition locator that will locate flow definitions needed for execution. Typically also a
	 * {@link FlowDefinitionRegistry}. Required.
	 * @param flowDefinitionLocator the flow definition locator (registry)
	 */
	public void setFlowDefinitionLocator(FlowDefinitionLocator flowDefinitionLocator) {
		this.flowDefinitionLocator = flowDefinitionLocator;
	}

	/**
	 * Sets the system attributes that apply to flow executions launched by the executor created by this factory.
	 * Execution attributes may affect flow execution behavior.
	 * @param flowExecutionAttributes the flow execution system attributes
	 */
	public void setFlowExecutionAttributes(Set flowExecutionAttributes) {
		this.flowExecutionAttributes = flowExecutionAttributes;
	}

	/**
	 * Sets the strategy for loading the listeners that will observe executions of a flow definition. Allows full
	 * control over what listeners should apply to executions of a flow definition launched by the executor created by
	 * this factory.
	 */
	public void setFlowExecutionListenerLoader(FlowExecutionListenerLoader flowExecutionListenerLoader) {
		this.flowExecutionListenerLoader = flowExecutionListenerLoader;
	}

	/**
	 * Sets the type of flow execution repository that should be configured for the flow executors created by this
	 * factory. This factory encapsulates the construction of the repository implementation corresponding to the
	 * provided type.
	 * @param repositoryType the flow execution repository type
	 */
	public void setFlowExecutionRepositoryType(FlowExecutionRepositoryType repositoryType) {
		this.flowExecutionRepositoryType = repositoryType;
	}

	/**
	 * Set the maximum number of continuation snapshots allowed for a single conversation when using the
	 */
	public void setMaxContinuations(int maxContinuations) {
		this.maxContinuations = new Integer(maxContinuations);
	}

	/**
	 * Set the maximum number of allowed concurrent conversations in the session. This is a convenience setter to allow
	 * easy configuration of the maxConversations property of the default {@link SessionBindingConversationManager}. Do
	 * not use this when an explicit conversation manager is configured.
	 * @see SessionBindingConversationManager#setMaxConversations(int)
	 */
	public void setMaxConversations(int maxConversations) {
		this.maxConversations = new Integer(maxConversations);
	}

	/**
	 * Sets the strategy for managing conversations that should be configured for flow executors created by this
	 * factory.
	 * <p>
	 * The conversation manager is used by the flow execution repository subsystem to begin and end new conversations
	 * that store execution state.
	 * <p>
	 * By default, a {@link SessionBindingConversationManager} is used. Do not use setMaxConversations when using this
	 * method.
	 */
	public void setConversationManager(ConversationManager conversationManager) {
		this.conversationManager = conversationManager;
	}

	/**
	 * Sets the conversion service for converting string-encoded flow execution attributes to typed values.
	 * @param conversionService the conversion service
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	// implementing InitializingBean

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(flowDefinitionLocator, "The flow definition locator property is required");

		// apply defaults
		FlowExecutorSystemDefaults defaults = new FlowExecutorSystemDefaults();

		MutableAttributeMap executionAttributes = defaults.applyExecutionAttributes(createExecutionAttributeMap());
		flowExecutionRepositoryType = defaults.applyIfNecessary(flowExecutionRepositoryType);

		// pass all available parameters to the hook methods so that they
		// can participate in the construction process

		// a strategy to restore deserialized flow executions
		FlowExecutionStateRestorer executionStateRestorer = createFlowExecutionStateRestorer(flowDefinitionLocator,
				executionAttributes, flowExecutionListenerLoader);

		// a repository to store flow executions
		FlowExecutionRepository executionRepository = createFlowExecutionRepository(flowExecutionRepositoryType,
				executionStateRestorer, conversationManager);

		// a factory for flow executions
		FlowExecutionFactory executionFactory = createFlowExecutionFactory(executionAttributes,
				flowExecutionListenerLoader, (FlowExecutionKeyFactory) executionRepository);

		// combine all pieces of the puzzle to get an operational flow executor
		flowExecutor = createFlowExecutor(flowDefinitionLocator, executionFactory, executionRepository);
	}

	// implementing FactoryBean

	public Class getObjectType() {
		return FlowExecutor.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public Object getObject() throws Exception {
		return flowExecutor;
	}

	// subclassing hook methods

	/**
	 * Create the flow execution state restorer to be used by the executor produced by this factory bean. Configure the
	 * state restorer appropriately. Subclasses may override if they which to use a custom state restorer
	 * implementation.
	 * @param definitionLocator the definition locator to use
	 * @param executionAttributes execution attributes to apply to restored executions
	 * @param executionListenerLoader decides which listeners should apply to restored flow executions
	 * @return a new state restorer instance
	 */
	protected FlowExecutionStateRestorer createFlowExecutionStateRestorer(FlowDefinitionLocator definitionLocator,
			AttributeMap executionAttributes, FlowExecutionListenerLoader executionListenerLoader) {
		FlowExecutionImplStateRestorer executionStateRestorer = new FlowExecutionImplStateRestorer(definitionLocator);
		executionStateRestorer.setExecutionAttributes(executionAttributes);
		if (executionListenerLoader != null) {
			executionStateRestorer.setExecutionListenerLoader(executionListenerLoader);
		}
		return executionStateRestorer;
	}

	/**
	 * Factory method for creating the flow execution repository for saving and loading executing flows. Subclasses may
	 * override to customize the repository implementation used.
	 * @param repositoryType a hint indicating what type of repository to create
	 * @param executionStateRestorer the execution state restorer strategy to be used by the repository
	 * @param conversationManager the conversation manager specified by the user, could be null in which case the
	 * default conversation manager should be used
	 * @return a new flow execution repository instance
	 */
	protected FlowExecutionRepository createFlowExecutionRepository(FlowExecutionRepositoryType repositoryType,
			FlowExecutionStateRestorer executionStateRestorer, ConversationManager conversationManager) {
		if (repositoryType == FlowExecutionRepositoryType.CLIENT) {
			throw new UnsupportedOperationException(
					"The 'client' flow execution repository is not supported in this 2.0 Milestone; support is planned for a future release");
		} else {
			// determine the conversation manager to use
			ConversationManager conversationManagerToUse = conversationManager;
			if (conversationManagerToUse == null) {
				conversationManagerToUse = createDefaultConversationManager();
			}
			if (repositoryType == FlowExecutionRepositoryType.SIMPLE) {
				DefaultFlowExecutionRepository repository = new DefaultFlowExecutionRepository(
						conversationManagerToUse, executionStateRestorer);
				repository.setMaxContinuations(1);
				return repository;
			} else if (repositoryType == FlowExecutionRepositoryType.CONTINUATION) {
				DefaultFlowExecutionRepository repository = new DefaultFlowExecutionRepository(
						conversationManagerToUse, executionStateRestorer);
				if (maxContinuations != null) {
					repository.setMaxContinuations(maxContinuations.intValue());
				}
				return repository;
			} else if (repositoryType == FlowExecutionRepositoryType.SINGLEKEY) {
				DefaultFlowExecutionRepository repository = new DefaultFlowExecutionRepository(
						conversationManagerToUse, executionStateRestorer);
				repository.setAlwaysGenerateNewNextKey(false);
				return repository;
			} else {
				throw new IllegalStateException("Cannot create execution repository - unsupported repository type "
						+ repositoryType);
			}
		}
	}

	/**
	 * Create the conversation manager to be used in the default case, e.g. when no explicit conversation manager has
	 * been configured. This implementation return a {@link SessionBindingConversationManager}.
	 * @return the default conversation manager
	 */
	protected ConversationManager createDefaultConversationManager() {
		SessionBindingConversationManager conversationManager = new SessionBindingConversationManager();
		if (maxConversations != null) {
			conversationManager.setMaxConversations(maxConversations.intValue());
		}
		return conversationManager;
	}

	/**
	 * Create the flow execution factory to be used by the executor produced by this factory bean. Configure the
	 * execution factory appropriately. Subclasses may override if they which to use a custom execution factory, e.g. to
	 * use a custom FlowExecution implementation.
	 * @param executionAttributes execution attributes to apply to created executions
	 * @param executionListenerLoader decides which listeners to apply to created executions
	 * @return a new flow execution factory instance
	 */
	protected FlowExecutionFactory createFlowExecutionFactory(AttributeMap executionAttributes,
			FlowExecutionListenerLoader executionListenerLoader, FlowExecutionKeyFactory keyFactory) {
		FlowExecutionImplFactory executionFactory = new FlowExecutionImplFactory();
		executionFactory.setExecutionAttributes(executionAttributes);
		if (executionListenerLoader != null) {
			executionFactory.setExecutionListenerLoader(executionListenerLoader);
		}
		executionFactory.setExecutionKeyFactory(keyFactory);
		return executionFactory;
	}

	/**
	 * Create the flow executor instance created by this factory bean and configure it appropriately. Subclasses may
	 * override if they which to use a custom executor implementation.
	 * @param definitionLocator the definition locator to use
	 * @param executionFactory the execution factory to use
	 * @param executionRepository the execution repository to use
	 * @return a new flow executor instance
	 */
	protected FlowExecutor createFlowExecutor(FlowDefinitionLocator definitionLocator,
			FlowExecutionFactory executionFactory, FlowExecutionRepository executionRepository) {
		return new FlowExecutorImpl(definitionLocator, executionFactory, executionRepository);
	}

	private MutableAttributeMap createExecutionAttributeMap() {
		LocalAttributeMap executionAttributes = new LocalAttributeMap();
		if (flowExecutionAttributes != null) {
			for (Iterator it = flowExecutionAttributes.iterator(); it.hasNext();) {
				FlowElementAttribute attribute = (FlowElementAttribute) it.next();
				executionAttributes.put(attribute.getName(), getConvertedValue(attribute));
			}
		}
		return executionAttributes;
	}

	private Object getConvertedValue(FlowElementAttribute attribute) {
		if (attribute.needsTypeConversion()) {
			ConversionExecutor converter = conversionService.getConversionExecutorByTargetAlias(String.class, attribute
					.getType());
			return converter.execute(attribute.getValue());
		} else {
			return attribute.getValue();
		}
	}
}
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

import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
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
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.factory.FlowExecutionListenerLoader;
import org.springframework.webflow.execution.factory.StaticFlowExecutionListenerLoader;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.repository.continuation.ClientContinuationFlowExecutionRepository;
import org.springframework.webflow.execution.repository.continuation.ContinuationFlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.FlowExecutionStateRestorer;
import org.springframework.webflow.execution.repository.support.SimpleFlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * The default flow executor factory implementation. As a <code>FactoryBean</code>,
 * this class has been designed for use as a Spring managed bean.
 * <p>
 * This factory encapsulates the construction and assembly of a
 * {@link FlowExecutor}, including the provision of its
 * {@link FlowExecutionRepository} strategy.
 * <p>
 * The {@link #setDefinitionLocator(FlowDefinitionLocator) definition locator}
 * property is required, all other properties are optional.
 * <p>
 * This class has been designed with subclassing in mind. If you want to do advanced
 * Spring Web Flow customization, e.g. using a custom
 * {@link org.springframework.webflow.executor.FlowExecutor} implementation,
 * consider subclassing this class and overriding one or more of the provided
 * hook methods.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class FlowExecutorFactoryBean implements FactoryBean, InitializingBean {

	/**
	 * The locator the executor will use to access flow definitions registered
	 * in a central registry. Required.
	 */
	private FlowDefinitionLocator definitionLocator;

	/**
	 * Execution attributes to apply.
	 */
	private MutableAttributeMap executionAttributes;
	
	/**
	 * The loader that will determine which listeners to attach to flow definition executions. 
	 */
	private FlowExecutionListenerLoader executionListenerLoader;
	
	/**
	 * The conversation manager to be used by the flow execution repository to
	 * store state associated with conversations driven by Spring Web Flow.
	 */
	private ConversationManager conversationManager;
	
	/**
	 * The maximum number of allowed concurrent conversations in the session.
	 */
	private Integer maxConversations;
	
	/**
	 * The type of execution repository to configure with executors created by
	 * this factory.  Optional.  Will fallback to default value if not set.
	 */
	private RepositoryType repositoryType;
	
	/**
	 * The maximum number of allowed continuations for a single conversation.
	 * Only used when the repository type is {@link RepositoryType#CONTINUATION}.
	 */
	private Integer maxContinuations;

	/**
	 * The flow executor this factory bean creates.
	 */
	private FlowExecutor flowExecutor;

	/**
	 * Spring Web Flow executor system defaults. 
	 */
	private FlowSystemDefaults defaults = new FlowSystemDefaults();
	
	/**
	 * Sets the flow definition locator that will locate flow definitions needed
	 * for execution. Typically also a {@link FlowDefinitionRegistry}. Required.
	 * @param definitionLocator the flow definition locator (registry)
	 */
	public void setDefinitionLocator(FlowDefinitionLocator definitionLocator) {
		this.definitionLocator = definitionLocator;
	}

	/**
	 * Sets the system attributes that apply to flow executions launched by the
	 * executor created by this factory. Execution attributes may affect flow
	 * execution behavior.
	 * <p>
	 * Note: this method simply accepts a generic <code>java.util.Map</code>
	 * to allow for easy configuration by Spring. The map entries should consist
	 * of non-null String keys with object values.
	 * @param executionAttributes the flow execution system attributes
	 */
	public void setExecutionAttributes(Map executionAttributes) {
		this.executionAttributes = new LocalAttributeMap(executionAttributes);
	}

	/**
	 * Convenience setter that sets a single listener that always applies to flow
	 * executions launched by the executor created by this factory.
	 * @param executionListener the flow execution listener
	 */
	public void setExecutionListener(FlowExecutionListener executionListener) {
		setExecutionListeners(new FlowExecutionListener[] { executionListener });
	}

	/**
	 * Convenience setter that sets a list of listeners that always apply to
	 * flow executions launched by the executor created by this factory.
	 * @param executionListeners the flow execution listeners
	 */
	public void setExecutionListeners(FlowExecutionListener[] executionListeners) {
		setExecutionListenerLoader(new StaticFlowExecutionListenerLoader(executionListeners));
	}

	/**
	 * Sets the strategy for loading the listeners that will observe executions
	 * of a flow definition. Allows full control over what listeners should
	 * apply to executions of a flow definition launched by the executor created
	 * by this factory.
	 */
	public void setExecutionListenerLoader(FlowExecutionListenerLoader executionListenerLoader) {
		this.executionListenerLoader = executionListenerLoader;
	}

	/**
	 * Sets the type of flow execution repository that should be configured for
	 * the flow executors created by this factory. This factory encapsulates the
	 * construction of the repository implementation corresponding to the
	 * provided type.
	 * @param repositoryType the flow execution repository type
	 */
	public void setRepositoryType(RepositoryType repositoryType) {
		this.repositoryType = repositoryType;
	}
	
	/**
	 * Set the maximum number of continuation snapshots allowed for a single
	 * conversation when using the {@link RepositoryType#CONTINUATION continuation}
	 * flow execution repository.
	 * @see ContinuationFlowExecutionRepository#setMaxContinuations(int)
	 */
	public void setMaxContinuations(int maxContinuations) {
		this.maxContinuations = new Integer(maxContinuations);
	}
	
	/**
	 * Returns the configured maximum number of continuation snapshots allowed
	 * for a single conversation when using the
	 * {@link RepositoryType#CONTINUATION continuation} flow execution repository.
	 * @return the configured value or null if the user did not explicitly
	 * specify a value and wants to use the default
	 */
	protected Integer getMaxContinuations() {
		return maxContinuations;
	}

	/**
	 * Sets the strategy for managing conversations that should be configured
	 * for flow executors created by this factory.
	 * <p>
	 * The conversation manager is used by the flow execution repository
	 * subsystem to begin and end new conversations that store execution state.
	 * <p>
	 * By default, a {@link SessionBindingConversationManager} is used. Do not
	 * use {@link #setMaxConversations(int)} when using this method.
	 */
	public void setConversationManager(ConversationManager conversationManager) {
		this.conversationManager = conversationManager;
	}
	
	/**
	 * Set the maximum number of allowed concurrent conversations in the session. This
	 * is a convenience setter to allow easy configuration of the maxConversations
	 * property of the default {@link SessionBindingConversationManager}. Do not use
	 * this when using {@link #setConversationManager(ConversationManager)}.
	 * @see SessionBindingConversationManager#setMaxConversations(int)
	 */
	public void setMaxConversations(int maxConversations) {
		this.maxConversations = new Integer(maxConversations);
	}
	
	/**
	 * Returns the configured maximum number of allowed concurrent conversations
	 * in the session. Will only be used when using the default conversation manager,
	 * e.g. when no explicit conversation manager has been configured using
	 * {@link #setConversationManager(ConversationManager)}.
	 * @return the configured value or null if the user did not explicitly 
	 * specify a value and wants to use the default
	 */
	protected Integer getMaxConversations() {
		return maxConversations;
	}
	
	/**
	 * Set system defaults that should be used.
	 * @param defaults the defaults to use.
	 */
	public void setDefaults(FlowSystemDefaults defaults) {
		this.defaults = defaults;
	}

	// implementing InitializingBean

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(definitionLocator, "The flow definition locator is required");
        
        // apply defaults
        executionAttributes = defaults.applyExecutionAttributes(executionAttributes);
        repositoryType = defaults.applyIfNecessary(repositoryType);
        
        // pass all available parameters to the hook methods so that they
        // can participate in the construction process
        
        // a factory for flow executions
        FlowExecutionFactory executionFactory =
            createFlowExecutionFactory(executionAttributes, executionListenerLoader);
        
        // a strategy to restore deserialized flow executions
        FlowExecutionStateRestorer executionStateRestorer =
            createFlowExecutionStateRestorer(definitionLocator, executionAttributes, executionListenerLoader);
        
        // a repository to store flow executions
        FlowExecutionRepository executionRepository =
            createExecutionRepository(repositoryType, executionStateRestorer, conversationManager);
        
        // combine all pieces of the puzzle to get an operational flow executor
        flowExecutor = createFlowExecutor(definitionLocator, executionFactory, executionRepository);
	}

	// subclassing hook methods
	
	/**
	 * Create the conversation manager to be used in the default case, e.g. when no
	 * explicit conversation manager has been configured using
	 * {@link #setConversationManager(ConversationManager)}. This implementation
	 * return a {@link SessionBindingConversationManager}.
	 * @return the default conversation manager
	 */
	protected ConversationManager createDefaultConversationManager() {
		SessionBindingConversationManager conversationManager = new SessionBindingConversationManager();
		if (getMaxConversations() != null) {
			conversationManager.setMaxConversations(getMaxConversations().intValue());
		}
		return conversationManager;
	}
    
    /**
     * Create the flow execution factory to be used by the executor produced by this
     * factory bean. Configure the execution factory appropriately. Subclasses may
     * override if they which to use a custom execution factory, e.g. to use a custom
     * FlowExecution implementation.
     * @param executionAttributes execution attributes to apply to created executions
     * @param executionListenerLoader decides which listeners to apply to created executions
     * @return a new flow execution factory instance
     */
    protected FlowExecutionFactory createFlowExecutionFactory(
            AttributeMap executionAttributes, FlowExecutionListenerLoader executionListenerLoader) {
        FlowExecutionImplFactory executionFactory = new FlowExecutionImplFactory();
        executionFactory.setExecutionAttributes(executionAttributes);
        if (executionListenerLoader != null) {
            executionFactory.setExecutionListenerLoader(executionListenerLoader);
        }
        return executionFactory;
    }
    
    /**
     * Create the flow execution state restorer to be used by the executor produced by
     * this factory bean. Configure the state restorer appropriately. Subclasses may
     * override if they which to use a custom state restorer implementation.
     * @param definitionLocator the definition locator to use
     * @param executionAttributes execution attributes to apply to restored executions
     * @param executionListenerLoader decides which listeners should apply to restored
     * flow executions
     * @return a new state restorer instance
     */
    protected FlowExecutionStateRestorer createFlowExecutionStateRestorer(
            FlowDefinitionLocator definitionLocator, AttributeMap executionAttributes,
            FlowExecutionListenerLoader executionListenerLoader) {
        FlowExecutionImplStateRestorer executionStateRestorer = new FlowExecutionImplStateRestorer(definitionLocator);
        executionStateRestorer.setExecutionAttributes(executionAttributes);
        if (executionListenerLoader != null) {
            executionStateRestorer.setExecutionListenerLoader(executionListenerLoader);
        }
        return executionStateRestorer;
    }

	/**
	 * Factory method for creating the flow execution repository for saving and
	 * loading executing flows. Subclasses may override to customize the
	 * repository implementation used.
     * @param repositoryType a hint indicating what type of repository to create
	 * @param executionStateRestorer the execution state restorer strategy to be used by
     * the repository
     * @param conversationManager the conversation manager specified by the user,
     * could be null in which case the default conversation manager should be used
	 * @return a new flow execution repository instance
	 */
	protected FlowExecutionRepository createExecutionRepository(
            RepositoryType repositoryType, FlowExecutionStateRestorer executionStateRestorer,
            ConversationManager conversationManager) {
		if (repositoryType == RepositoryType.CLIENT) {
			if (conversationManager == null) {
				// use the default no-op conversation manager
				return new ClientContinuationFlowExecutionRepository(executionStateRestorer);
			}
			else {
				// use the conversation manager specified by the user
				return new ClientContinuationFlowExecutionRepository(executionStateRestorer, conversationManager);
			}
		}
		else {
			// determine the conversation manager to use
			ConversationManager conversationManagerToUse = conversationManager;
			if (conversationManagerToUse == null) {
				conversationManagerToUse = createDefaultConversationManager();
			}
			
			if (repositoryType == RepositoryType.SIMPLE) {
				return new SimpleFlowExecutionRepository(executionStateRestorer, conversationManagerToUse);
			}
			else if (repositoryType == RepositoryType.CONTINUATION) {
				ContinuationFlowExecutionRepository repository =
					new ContinuationFlowExecutionRepository(executionStateRestorer, conversationManagerToUse);
				if (getMaxContinuations() != null) {
					repository.setMaxContinuations(getMaxContinuations().intValue());
				}
				return repository;
			}
			else if (repositoryType == RepositoryType.SINGLEKEY) {
				SimpleFlowExecutionRepository repository = new SimpleFlowExecutionRepository(
						executionStateRestorer, conversationManagerToUse);
				repository.setAlwaysGenerateNewNextKey(false);
				return repository;
			}
			else {
				throw new IllegalStateException("Cannot create execution repository - unsupported repository type "
						+ repositoryType);
			}
		}
	}
    
    /**
     * Create the flow executor instance created by this factory bean and configure
     * it appropriately. Subclasses may override if they which to use a custom executor
     * implementation.
     * @param definitionLocator the definition locator to use
     * @param executionFactory the execution factory to use
     * @param executionRepository the execution repository to use
     * @return a new flow executor instance
     */
    protected FlowExecutor createFlowExecutor(
            FlowDefinitionLocator definitionLocator, FlowExecutionFactory executionFactory,
            FlowExecutionRepository executionRepository) {
        return new FlowExecutorImpl(definitionLocator, executionFactory, executionRepository);
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
}
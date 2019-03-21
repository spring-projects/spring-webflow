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
package org.springframework.webflow.config;

import java.util.Set;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.factory.FlowExecutionListenerLoader;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.repository.impl.DefaultFlowExecutionRepository;
import org.springframework.webflow.execution.repository.snapshot.FlowExecutionSnapshotFactory;
import org.springframework.webflow.execution.repository.snapshot.SerializedFlowExecutionSnapshotFactory;
import org.springframework.webflow.execution.repository.snapshot.SimpleFlowExecutionSnapshotFactory;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * This factory encapsulates the construction and assembly of a {@link FlowExecutor}, including the provision of its
 * {@link FlowExecutionRepository} strategy. As a <code>FactoryBean</code>, this class has been designed for use as a
 * Spring managed bean.
 * <p>
 * The definition locator property is required, all other properties are optional.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
class FlowExecutorFactoryBean implements FactoryBean<FlowExecutor>, BeanClassLoaderAware, InitializingBean {

	private static final String ALWAYS_REDIRECT_ON_PAUSE = "alwaysRedirectOnPause";

	private static final String REDIRECT_IN_SAME_STATE = "redirectInSameState";

	private FlowDefinitionLocator flowDefinitionLocator;

	private Integer maxFlowExecutions;

	private Integer maxFlowExecutionSnapshots;

	private Set<FlowElementAttribute> flowExecutionAttributes;

	private FlowExecutionListenerLoader flowExecutionListenerLoader;

	private ConversationManager conversationManager;

	private ConversionService conversionService;

	private FlowExecutor flowExecutor;

	private ClassLoader classLoader;

	/**
	 * Sets the flow definition locator that will locate flow definitions needed for execution. Typically also a
	 * {@link FlowDefinitionRegistry}. Required.
	 * @param flowDefinitionLocator the flow definition locator (registry)
	 */
	public void setFlowDefinitionLocator(FlowDefinitionLocator flowDefinitionLocator) {
		this.flowDefinitionLocator = flowDefinitionLocator;
	}

	/**
	 * Set the maximum number of allowed flow executions allowed per user.
	 */
	public void setMaxFlowExecutions(int maxFlowExecutions) {
		this.maxFlowExecutions = maxFlowExecutions;
	}

	/**
	 * Set the maximum number of history snapshots allowed per flow execution.
	 */
	public void setMaxFlowExecutionSnapshots(int maxFlowExecutionSnapshots) {
		this.maxFlowExecutionSnapshots = maxFlowExecutionSnapshots;
	}

	/**
	 * Sets the system attributes that apply to flow executions launched by the executor created by this factory.
	 * Execution attributes may affect flow execution behavior.
	 * @param flowExecutionAttributes the flow execution system attributes
	 */
	public void setFlowExecutionAttributes(Set<FlowElementAttribute> flowExecutionAttributes) {
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
	 * Sets the service type that manages conversations and effectively controls how state is stored physically when a
	 * flow execution is paused.
	 */
	public void setConversationManager(ConversationManager conversationManager) {
		this.conversationManager = conversationManager;
	}

	// implement BeanClassLoaderAware

	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	// implementing InitializingBean

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(flowDefinitionLocator, "The flow definition locator property is required");
		if (conversionService == null) {
			conversionService = new DefaultConversionService();
		}
		MutableAttributeMap<Object> executionAttributes = createFlowExecutionAttributes();
		FlowExecutionImplFactory executionFactory = createFlowExecutionFactory(executionAttributes);
		DefaultFlowExecutionRepository executionRepository = createFlowExecutionRepository(executionFactory);
		executionFactory.setExecutionKeyFactory(executionRepository);
		flowExecutor = new FlowExecutorImpl(flowDefinitionLocator, executionFactory, executionRepository);
	}

	// implementing FactoryBean

	public Class<?> getObjectType() {
		return FlowExecutor.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public FlowExecutor getObject() throws Exception {
		return flowExecutor;
	}

	private MutableAttributeMap<Object> createFlowExecutionAttributes() {
		LocalAttributeMap<Object> executionAttributes = new LocalAttributeMap<>();
		if (flowExecutionAttributes != null) {
			for (FlowElementAttribute attribute : flowExecutionAttributes) {
				executionAttributes.put(attribute.getName(), getConvertedValue(attribute));
			}
		}
		putDefaultFlowExecutionAttributes(executionAttributes);
		return executionAttributes;
	}

	private void putDefaultFlowExecutionAttributes(LocalAttributeMap<Object> executionAttributes) {
		if (!executionAttributes.contains(ALWAYS_REDIRECT_ON_PAUSE)) {
			executionAttributes.put(ALWAYS_REDIRECT_ON_PAUSE, true);
		}
		if (!executionAttributes.contains(REDIRECT_IN_SAME_STATE)) {
			executionAttributes.put(REDIRECT_IN_SAME_STATE, true);
		}
	}

	private DefaultFlowExecutionRepository createFlowExecutionRepository(FlowExecutionFactory executionFactory) {
		ConversationManager conversationManager = createConversationManager();
		FlowExecutionSnapshotFactory snapshotFactory = createFlowExecutionSnapshotFactory(executionFactory);
		DefaultFlowExecutionRepository rep = new DefaultFlowExecutionRepository(conversationManager, snapshotFactory);
		if (maxFlowExecutionSnapshots != null) {
			rep.setMaxSnapshots(maxFlowExecutionSnapshots);
		}
		return rep;
	}

	private ConversationManager createConversationManager() {
		if (conversationManager == null) {
			conversationManager = new SessionBindingConversationManager();
			if (maxFlowExecutions != null) {
				((SessionBindingConversationManager) conversationManager).setMaxConversations(maxFlowExecutions);
			}
		}
		return this.conversationManager;
	}

	private FlowExecutionSnapshotFactory createFlowExecutionSnapshotFactory(FlowExecutionFactory executionFactory) {
		if (maxFlowExecutionSnapshots != null && maxFlowExecutionSnapshots == 0) {
			maxFlowExecutionSnapshots = 1;
			return new SimpleFlowExecutionSnapshotFactory(executionFactory, flowDefinitionLocator);
		} else {
			return new SerializedFlowExecutionSnapshotFactory(executionFactory, flowDefinitionLocator);
		}
	}

	private FlowExecutionImplFactory createFlowExecutionFactory(AttributeMap<Object> executionAttributes) {
		FlowExecutionImplFactory executionFactory = new FlowExecutionImplFactory();
		executionFactory.setExecutionAttributes(executionAttributes);
		if (flowExecutionListenerLoader != null) {
			executionFactory.setExecutionListenerLoader(flowExecutionListenerLoader);
		}
		return executionFactory;
	}

	// utility methods

	private Object getConvertedValue(FlowElementAttribute attribute) {
		if (attribute.needsTypeConversion()) {
			Class<?> targetType = fromStringToClass(attribute.getType());
			ConversionExecutor converter = conversionService.getConversionExecutor(String.class, targetType);
			return converter.execute(attribute.getValue());
		} else {
			return attribute.getValue();
		}
	}

	private Class<?> fromStringToClass(String name) {
		Class<?> clazz = conversionService.getClassForAlias(name);
		if (clazz != null) {
			return clazz;
		} else {
			try {
				return ClassUtils.forName(name, classLoader);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Unable to load class '" + name + "'");
			}
		}
	}

}

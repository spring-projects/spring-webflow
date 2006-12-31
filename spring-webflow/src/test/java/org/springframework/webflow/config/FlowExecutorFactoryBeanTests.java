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

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.webflow.conversation.ConversationManager;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.repository.continuation.ClientContinuationFlowExecutionRepository;
import org.springframework.webflow.execution.repository.continuation.ContinuationFlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.SimpleFlowExecutionRepository;
import org.springframework.webflow.executor.FlowExecutorImpl;

/**
 * Test case for {@link FlowExecutorFactoryBean}.
 * 
 * @author Erwin Vervaet
 */
public class FlowExecutorFactoryBeanTests extends TestCase {
	
	private ApplicationContext applicationContext;
	
	protected void setUp() throws Exception {
		this.applicationContext =
			new ClassPathXmlApplicationContext("flow-executor-factory-bean.xml", BeanConfigTests.class);
	}
	
	public void testSetMaxConversationsAndMaxContinuations() {
		FlowExecutorImpl flowExecutor = (FlowExecutorImpl)applicationContext.getBean("flowExecutor0");
		assertTrue(flowExecutor.getExecutionRepository() instanceof ContinuationFlowExecutionRepository);
		ContinuationFlowExecutionRepository repo =
			(ContinuationFlowExecutionRepository)flowExecutor.getExecutionRepository();
		SessionBindingConversationManager conversationManager =
			(SessionBindingConversationManager)repo.getConversationManager();
		assertEquals(1, conversationManager.getMaxConversations());
		assertEquals(10, repo.getMaxContinuations());
	}
	
	public void testRepoConfigurationWithDefaultConversationManager() throws Exception {
		SimpleFlowExecutionRepository simple =
			(SimpleFlowExecutionRepository)setupRepo(RepositoryType.SIMPLE, null);
		assertTrue(simple.getConversationManager() instanceof SessionBindingConversationManager);
		assertTrue(simple.isAlwaysGenerateNewNextKey());
		
		SimpleFlowExecutionRepository singleKey =
			(SimpleFlowExecutionRepository)setupRepo(RepositoryType.SINGLEKEY, null);
		assertTrue(singleKey.getConversationManager() instanceof SessionBindingConversationManager);
		assertFalse(singleKey.isAlwaysGenerateNewNextKey());
		
		ContinuationFlowExecutionRepository continuation =
			(ContinuationFlowExecutionRepository)setupRepo(RepositoryType.CONTINUATION, null);
		assertTrue(continuation.getConversationManager() instanceof SessionBindingConversationManager);
		
		ClientContinuationFlowExecutionRepository client =
			(ClientContinuationFlowExecutionRepository)setupRepo(RepositoryType.CLIENT, null);
		assertFalse(
				"Client repo does not use SessionBindingConversationManager by default",
				client.getConversationManager() instanceof SessionBindingConversationManager);
	}
	
	public void testRepoConfiguration() throws Exception {
		ConversationManager cm = new CustomConversationManager();
		
		SimpleFlowExecutionRepository simple =
			(SimpleFlowExecutionRepository)setupRepo(RepositoryType.SIMPLE, cm);
		assertTrue(simple.getConversationManager() instanceof CustomConversationManager);
		assertTrue(simple.isAlwaysGenerateNewNextKey());
		
		SimpleFlowExecutionRepository singleKey =
			(SimpleFlowExecutionRepository)setupRepo(RepositoryType.SINGLEKEY, cm);
		assertTrue(singleKey.getConversationManager() instanceof CustomConversationManager);
		assertFalse(singleKey.isAlwaysGenerateNewNextKey());
		
		ContinuationFlowExecutionRepository continuation =
			(ContinuationFlowExecutionRepository)setupRepo(RepositoryType.CONTINUATION, cm);
		assertTrue(continuation.getConversationManager() instanceof CustomConversationManager);
		
		ClientContinuationFlowExecutionRepository client =
			(ClientContinuationFlowExecutionRepository)setupRepo(RepositoryType.CLIENT, cm);
		assertTrue(client.getConversationManager() instanceof CustomConversationManager);
	}
		
	private FlowExecutionRepository setupRepo(
			RepositoryType repoType, ConversationManager conversationManager) throws Exception {
		FlowExecutorFactoryBean flowExecutorFactoryBean = new FlowExecutorFactoryBean();
		flowExecutorFactoryBean.setDefinitionLocator(
				(FlowDefinitionLocator)applicationContext.getBean("flowRegistry"));
		flowExecutorFactoryBean.setRepositoryType(repoType);
		if (conversationManager != null) {
			flowExecutorFactoryBean.setConversationManager(conversationManager);
		}
		flowExecutorFactoryBean.afterPropertiesSet();
		FlowExecutorImpl flowExecutor = (FlowExecutorImpl)flowExecutorFactoryBean.getObject();
		return flowExecutor.getExecutionRepository();
	}

	private static class CustomConversationManager extends SessionBindingConversationManager {
	}
}

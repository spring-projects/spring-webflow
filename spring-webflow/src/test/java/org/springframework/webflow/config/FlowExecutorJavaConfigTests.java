/*
 * Copyright 2004-2014 the original author or authors.
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

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * Tests for configuring Web Flow by extending {@link AbstractFlowConfiguration}.
 *
 * @author Rossen Stoyanchev
 */
public class FlowExecutorJavaConfigTests extends AbstractFlowExecutorConfigurationTests {

	@Override
	protected ApplicationContext initApplicationContext() {
		return new AnnotationConfigApplicationContext(WebFlowConfig.class);
	}


	@Configuration
	static class WebFlowConfig extends AbstractFlowConfiguration {

		@Bean
		public FlowExecutor flowExecutor() {
			return getFlowExecutorBuilder(flowRegistry())
					.setMaxFlowExecutions(1).setMaxFlowExecutionSnapshots(2)
					.setConversationManager(new ExceptionThrowingConversationManager())
					.setAlwaysRedirectOnPause(false)
					.setRedirectInSameState(true)
					.addFlowExecutionAttribute("foo", "bar")
					.addFlowExecutionAttribute("bar", 2)
					.addFlowExecutionListener(new ConfigurationListener(), "*")
					.build();
		}

		@Bean
		public FlowDefinitionRegistry flowRegistry() {
			return getFlowDefinitionRegistryBuilder()
					.addFlowLocation("org/springframework/webflow/config/flow.xml").build();
		}

		@Bean
		public FlowExecutor flowExecutorSimpleRepo() {
			return getFlowExecutorBuilder(flowRegistry())
					.setMaxFlowExecutions(1).setMaxFlowExecutionSnapshots(0)
					.build();
		}
	}

}

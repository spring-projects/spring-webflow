/*
 * Copyright 2004-2024 the original author or authors.
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

package org.springframework.webflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.webflow.config.AbstractFlowConfiguration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Integration tests for {@link MultiAction}.
 *
 * @author Sam Brannen
 * @since 3.0.1
 * @see <a href="https://github.com/spring-projects/spring-webflow/issues/1802">gh-1802</a>
 */
@SpringJUnitConfig
@DirtiesContext
class MultiActionIntegrationTests {

	private static final String WITH_REQUEST_CONTEXT = "withRequestContext";

	private static final String WITHOUT_REQUEST_CONTEXT = "withoutRequestContext";


	@Autowired
	FlowExecutor flowExecutor;

	@Autowired
	CountingMultiAction multiAction;


	@BeforeEach
	void resetCounters() {
		multiAction.counterWithRequestContext = 0;
		multiAction.counterWithoutRequestContext = 0;
	}

	@Test
	void spelExpressionsWithRequestContext() {
		assertCounters(0, 0);
		launchFlowExecution(WITH_REQUEST_CONTEXT);
		assertCounters(2, 0);
	}

	@Test
	void spelExpressionsWithoutRequestContext() {
		assertCounters(0, 0);
		launchFlowExecution(WITHOUT_REQUEST_CONTEXT);
		assertCounters(0, 2);
	}

	private void launchFlowExecution(String flowId) {
		flowExecutor.launchExecution(flowId, null, new MockExternalContext());
	}

	private void assertCounters(int counterWithRequestContext, int counterWithoutRequestContext) {
		assertEquals(counterWithRequestContext, multiAction.counterWithRequestContext, "counterWithRequestContext");
		assertEquals(counterWithoutRequestContext, multiAction.counterWithoutRequestContext, "counterWithoutRequestContext");
	}



	@Configuration
	static class WebFlowConfig extends AbstractFlowConfiguration {

		@Bean
		CountingMultiAction countingMultiAction() {
			return new CountingMultiAction();
		}

		@Bean
		FlowExecutor flowExecutor() {
			return getFlowExecutorBuilder(flowRegistry()).build();
		}

		@Bean
		FlowDefinitionRegistry flowRegistry() {
			return getFlowDefinitionRegistryBuilder()
					.setBasePath("classpath:/org/springframework/webflow/action")
					.addFlowLocation("multi-action-with-request-context.xml", WITH_REQUEST_CONTEXT)
					.addFlowLocation("multi-action-without-request-context.xml", WITHOUT_REQUEST_CONTEXT)
					.build();
		}
	}

	@Component("countingMultiAction")
	static class CountingMultiAction extends MultiAction {

		int counterWithRequestContext = 0;

		int counterWithoutRequestContext = 0;

		public Event incrementWithRequestContext(RequestContext context) {
			counterWithRequestContext++;
			return success();
		}

		public Event incrementWithoutRequestContext() {
			counterWithoutRequestContext++;
			return success();
		}
	}

}

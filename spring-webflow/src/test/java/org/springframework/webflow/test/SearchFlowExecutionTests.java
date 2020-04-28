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
package org.springframework.webflow.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

/**
 * Sample {@link AbstractXmlFlowExecutionTests} subclass.
 */
public class SearchFlowExecutionTests extends AbstractXmlFlowExecutionTests {

	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createClassPathResource("search-flow.xml", getClass());
	}

	@Test
	public void testStartFlow() {
		ExternalContext context = new MockExternalContext();
		startFlow(null, context);
		assertCurrentStateEquals("enterCriteria");
	}

	@Test
	public void testCriteriaSubmitSuccess() {
		startFlow(null, new MockExternalContext());
		MockExternalContext context = new MockExternalContext();
		context.putRequestParameter("firstName", "Keith");
		context.putRequestParameter("lastName", "Donald");
		context.setEventId("search");
		resumeFlow(context);
		assertCurrentStateEquals("displayResults");
		assertResponseWrittenEquals("searchResults", context);
	}

	@Test
	public void testNewSearch() {
		startFlow(null, new MockExternalContext());
		MockExternalContext context = new MockExternalContext();
		context.putRequestParameter("firstName", "Keith");
		context.putRequestParameter("lastName", "Donald");
		context.setEventId("search");
		resumeFlow(context);

		context = new MockExternalContext();
		context.setEventId("newSearch");
		resumeFlow(context);
		assertCurrentStateEquals("enterCriteria");
		assertResponseWrittenEquals("searchCriteria", context);
	}

	@Test
	public void testSelectValidResult() {
		startFlow(null, new MockExternalContext());
		MockExternalContext context = new MockExternalContext();
		context.putRequestParameter("firstName", "Keith");
		context.putRequestParameter("lastName", "Donald");
		context.setEventId("search");
		resumeFlow(context);

		context = new MockExternalContext();
		context.setEventId("select");
		context.putRequestParameter("id", "1");
		resumeFlow(context);
		assertCurrentStateEquals("displayResults");
	}

	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		Flow mockDetailFlow = new Flow("detail-flow");
		mockDetailFlow.setInputMapper((source, target) -> {
			assertEquals("id of value 1 not provided as input by calling search flow", 1L, ((AttributeMap<?>) source).get("id"));
			return null;
		});
		// test responding to finish result
		new EndState(mockDetailFlow, "finish");
		builderContext.registerSubflow(mockDetailFlow);
		builderContext.registerBean("phonebook", new TestPhoneBook());
	}

	public static class TestPhoneBook {
		public List<Object> search(Object criteria) {
			ArrayList<Object> res = new ArrayList<>();
			res.add(new Object());
			return res;
		}

		public Object getPerson(Long id) {
			return new Object();
		}

		public Object getPerson(String userId) {
			return new Object();
		}
	}
}

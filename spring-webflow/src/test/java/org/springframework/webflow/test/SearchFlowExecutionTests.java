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
package org.springframework.webflow.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.binding.mapping.MappingContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

/**
 * Sample {@link AbstractXmlFlowExecutionTests} subclass.
 */
public class SearchFlowExecutionTests extends AbstractXmlFlowExecutionTests {

	public void testStartFlow() {
		ApplicationView view = applicationView(startFlow());
		assertCurrentStateEquals("enterCriteria");
		assertViewNameEquals("searchCriteria", view);
		assertModelAttributeNotNull("searchCriteria", view);
	}

	public void testCriteriaSubmitSuccess() {
		startFlow();
		MockParameterMap parameters = new MockParameterMap();
		parameters.put("firstName", "Keith");
		parameters.put("lastName", "Donald");
		ApplicationView view = applicationView(signalEvent("search", parameters));
		assertCurrentStateEquals("displayResults");
		assertViewNameEquals("searchResults", view);
		assertModelAttributeCollectionSize(1, "results", view);
	}

	public void testNewSearch() {
		testCriteriaSubmitSuccess();
		ApplicationView view = applicationView(signalEvent("newSearch"));
		assertCurrentStateEquals("enterCriteria");
		assertViewNameEquals("searchCriteria", view);
	}

	public void testSelectValidResult() {
		testCriteriaSubmitSuccess();
		MockParameterMap parameters = new MockParameterMap();
		parameters.put("id", "1");
		ApplicationView view = applicationView(signalEvent("select", parameters));
		assertCurrentStateEquals("displayResults");
		assertViewNameEquals("searchResults", view);
		assertModelAttributeCollectionSize(1, "results", view);
	}

	protected FlowDefinitionResource getFlowDefinitionResource() {
		return new FlowDefinitionResource("search-flow",
				new ClassPathResource("search-flow.xml", SearchFlowExecutionTests.class));
	}

	protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
		Flow mockDetailFlow = new Flow("detail-flow");
		mockDetailFlow.setInputMapper(new AttributeMapper() {
			public void map(Object source, Object target, MappingContext context) {
				assertEquals("id of value 1 not provided as input by calling search flow", new Long(1), ((AttributeMap)source).get("id"));
			}
		});
		// test responding to finish result
		new EndState(mockDetailFlow, "finish");

		serviceRegistry.registerSubflow(mockDetailFlow);
		serviceRegistry.registerBean("phonebook", new TestPhoneBook());
	}
	
	public static class TestPhoneBook {
		
		public List search(Object criteria) {
			ArrayList res = new ArrayList();
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
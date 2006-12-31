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
package org.springframework.webflow.test.execution;

import org.springframework.core.io.Resource;
import org.springframework.webflow.engine.builder.FlowBuilder;
import org.springframework.webflow.engine.builder.FlowServiceLocator;
import org.springframework.webflow.engine.builder.xml.XmlFlowBuilder;

/**
 * Base class for flow integration tests that verify an XML flow definition
 * executes as expected.
 * <p>
 * Example usage:
 * 
 * <pre>
 * public class SearchFlowExecutionTests extends AbstractXmlFlowExecutionTests {
 * 
 *     protected FlowDefinitionResource getFlowDefinitionResource() {
 *         return createFlowDefinitionResource("src/main/webapp/WEB-INF/flows/search-flow.xml");
 *     }
 * 
 *     public void testStartFlow() {
 *         startFlow();
 *         assertCurrentStateEquals(&quot;displaySearchCriteria&quot;);
 *     }
 * 
 *     public void testDisplayCriteriaSubmitSuccess() {
 *         startFlow();
 *         MockParameterMap parameters = new MockParameterMap();
 *         parameters.put(&quot;firstName&quot;, &quot;Keith&quot;);
 *         parameters.put(&quot;lastName&quot;, &quot;Donald&quot;);
 *         ViewSelection view = signalEvent(&quot;search&quot;, parameters);
 *         assertCurrentStateEquals(&quot;displaySearchResults&quot;);
 *         assertModelAttributeCollectionSize(1, &quot;results&quot;, view);
 *     } 
 * }
 * </pre>
 * 
 * @author Keith Donald
 */
public abstract class AbstractXmlFlowExecutionTests extends AbstractExternalizedFlowExecutionTests {
	
	protected final FlowBuilder createFlowBuilder(Resource resource, FlowServiceLocator flowServiceLocator) {
		return new XmlFlowBuilder(resource, flowServiceLocator);
	}
}
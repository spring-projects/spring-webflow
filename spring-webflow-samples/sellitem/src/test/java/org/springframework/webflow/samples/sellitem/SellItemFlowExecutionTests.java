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
package org.springframework.webflow.samples.sellitem;

import org.easymock.EasyMock;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockFlowServiceLocator;
import org.springframework.webflow.test.MockParameterMap;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

public class SellItemFlowExecutionTests extends AbstractXmlFlowExecutionTests {

	private String flowDir = "src/main/webapp/WEB-INF/flows";

	private SaleProcessor saleProcessor;

	@Override
	protected FlowDefinitionResource getFlowDefinitionResource() {
		return createFlowDefinitionResource(flowDir, "sellitem-flow.xml");
	}

	public void testStartFlow() {
		ApplicationView selectedView = applicationView(startFlow());
		assertModelAttributeNotNull("sale", selectedView);
		assertViewNameEquals("priceAndItemCountForm", selectedView);
	}

	public void testSubmitPriceAndItemCount() {
		testStartFlow();
		MockParameterMap parameters = new MockParameterMap();
		parameters.put("itemCount", "4");
		parameters.put("price", "25");
		ApplicationView selectedView = applicationView(signalEvent("submit", parameters));
		assertViewNameEquals("categoryForm", selectedView);
	}

	public void testSubmitCategoryForm() {
		testSubmitPriceAndItemCount();
		MockParameterMap parameters = new MockParameterMap();
		parameters.put("category", "A");
		ApplicationView selectedView = applicationView(signalEvent("submit", parameters));
		assertViewNameEquals("costOverview", selectedView);
		assertFlowExecutionEnded();
	}

	public void testSubmitCategoryFormWithShipping() {
		testSubmitPriceAndItemCount();
		MockParameterMap parameters = new MockParameterMap();
		parameters.put("category", "A");
		parameters.put("shipping", "true");
		ApplicationView selectedView = applicationView(signalEvent("submit", parameters));
		assertViewNameEquals("shippingDetailsForm", selectedView);
	}

	public void testSubmitShippingDetailsForm() {
		testSubmitCategoryFormWithShipping();

		saleProcessor.process((Sale)getRequiredFlowAttribute("sale", Sale.class));
		EasyMock.replay(saleProcessor);
		
		MockParameterMap parameters = new MockParameterMap();
		parameters.put("shippingType", "E");
		parameters.put("shipDate", "12/06/2007");
		ApplicationView selectedView = applicationView(signalEvent("submit", parameters));
		assertViewNameEquals("costOverview", selectedView);
		assertFlowExecutionEnded();

		EasyMock.verify(saleProcessor);
	}

	@Override
	protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
		saleProcessor = EasyMock.createMock(SaleProcessor.class);
		serviceRegistry.registerBean("saleProcessor", saleProcessor);

		// we'll use real shipping flow
		FlowDefinitionResource shipping = createFlowDefinitionResource(flowDir, "shipping-flow.xml");
		serviceRegistry.registerSubflow(createFlow(shipping, serviceRegistry));
	}
}
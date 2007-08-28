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
package org.springframework.webflow.engine.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.test.MockFlowServiceLocator;

/**
 * Tests that the factory bean properly creates a {@link FlowDefinitionRegistry} with the proper definitions in it.
 */
public class FlowRegistryFactoryBeanTests extends TestCase {

	public void testXmlRegistrar() throws Exception {
		Set emptyNamespace = new HashSet();
		emptyNamespace.add(new FlowDefinitionResource(fromClassPath("flow1.xml")));
		Set bookingNamespace = new HashSet();
		bookingNamespace.add(new FlowDefinitionResource(fromClassPath("flow2.xml")));
		Map xmlNamespaceFlowMappings = new HashMap();
		xmlNamespaceFlowMappings.put("", emptyNamespace);
		xmlNamespaceFlowMappings.put("booking", bookingNamespace);
		FlowRegistryFactoryBean factoryBean = new FlowRegistryFactoryBean();
		factoryBean.setFlowServiceLocator(new MockFlowServiceLocator());
		factoryBean.setXmlNamespaceFlowMappings(xmlNamespaceFlowMappings);
		factoryBean.afterPropertiesSet();
		FlowDefinitionRegistry registry = (FlowDefinitionRegistry) factoryBean.getObject();
		assertEquals("Incorrect number of flows", 2, registry.getFlowDefinitionCount());
		assertTrue("Missing flow", registry.containsFlowDefinition("flow1"));
		assertTrue("Missing flow", registry.containsFlowDefinition("booking/flow2"));
	}

	private Resource fromClassPath(String resourceName) {
		return new ClassPathResource(resourceName, getClass());
	}

}

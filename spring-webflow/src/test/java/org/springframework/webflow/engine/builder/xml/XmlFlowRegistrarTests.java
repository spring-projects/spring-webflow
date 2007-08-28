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
package org.springframework.webflow.engine.builder.xml;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.engine.builder.BaseFlowServiceLocator;

public class XmlFlowRegistrarTests extends TestCase {

	private XmlFlowRegistrar registrar;

	private FlowDefinitionRegistry registry = new FlowDefinitionRegistryImpl();

	protected void setUp() {
		BaseFlowServiceLocator locator = new BaseFlowServiceLocator();
		registrar = new XmlFlowRegistrar(locator);
	}

	public void testAddResource() {
		assertEquals(0, registry.getFlowDefinitionCount());
		registrar.addResource(new FlowDefinitionResource("foo", fromClassPath("flow.xml")), "namespace");
		registrar.registerFlowDefinitions(registry);
		assertEquals(1, registry.getFlowDefinitionCount());
		assertEquals("foo", registry.getFlowDefinition("namespace/foo").getId());
	}

	public void testAddResourceDefaultNamespace() {
		assertEquals(0, registry.getFlowDefinitionCount());
		registrar.setDefaultNamespace("default");
		registrar.addResource(new FlowDefinitionResource("foo", fromClassPath("flow.xml")));
		registrar.registerFlowDefinitions(registry);
		assertEquals(1, registry.getFlowDefinitionCount());
		assertEquals("foo", registry.getFlowDefinition("default/foo").getId());
	}

	public void testAddLocation() {
		assertEquals(0, registry.getFlowDefinitionCount());
		registrar.addLocation(fromClassPath("flow.xml"), "namespace");
		registrar.registerFlowDefinitions(registry);
		assertEquals(1, registry.getFlowDefinitionCount());
		assertEquals("flow", registry.getFlowDefinition("namespace/flow").getId());
	}

	public void testAddLocationDefaultNamespace() {
		assertEquals(0, registry.getFlowDefinitionCount());
		registrar.setDefaultNamespace("default");
		registrar.addLocation(fromClassPath("flow.xml"));
		registrar.registerFlowDefinitions(registry);
		assertEquals(1, registry.getFlowDefinitionCount());
		assertEquals("flow", registry.getFlowDefinition("default/flow").getId());
	}

	private Resource fromClassPath(String resourceName) {
		return new ClassPathResource(resourceName, getClass());
	}
}
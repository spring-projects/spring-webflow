package org.springframework.webflow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.test.TestFlowBuilderServicesFactory;

public class FlowRegistryFactoryBeanTests {
	private FlowRegistryFactoryBean factoryBean;

	@BeforeEach
	public void setUp() {
		factoryBean = new FlowRegistryFactoryBean();
	}

	@AfterEach
	public void tearDown() throws Exception {
		factoryBean.destroy();
	}

	@Test
	public void testGetFlowRegistry() throws Exception {
		HashSet<FlowElementAttribute> attributes = new HashSet<>();
		attributes.add(new FlowElementAttribute("foo", "bar", null));
		attributes.add(new FlowElementAttribute("bar", "2", "integer"));
		FlowLocation location1 = new FlowLocation("flow1", "org/springframework/webflow/config/flow.xml", attributes);
		FlowLocation location2 = new FlowLocation("flow2", "org/springframework/webflow/config/flow.xml", attributes);
		FlowLocation[] flowLocations = new FlowLocation[] { location1, location2 };
		factoryBean.setFlowBuilderServices(TestFlowBuilderServicesFactory.getServices());
		factoryBean.setFlowLocations(flowLocations);
		factoryBean.afterPropertiesSet();
		FlowDefinitionRegistry registry = factoryBean.getObject();
		FlowDefinition def = registry.getFlowDefinition("flow1");
		assertNotNull(def);
		assertEquals("flow1", def.getId());
		assertEquals("bar", def.getAttributes().get("foo"));
		assertEquals(new Integer(2), def.getAttributes().getInteger("bar"));
		def = registry.getFlowDefinition("flow2");
		assertNotNull(def);
		assertEquals("flow2", def.getId());
	}

	@Test
	public void testGetFlowRegistryGeneratedFlowId() throws Exception {
		FlowLocation location1 = new FlowLocation(null, "org/springframework/webflow/config/flow.xml", null);
		FlowLocation[] flowLocations = new FlowLocation[] { location1 };
		factoryBean.setFlowBuilderServices(TestFlowBuilderServicesFactory.getServices());
		factoryBean.setFlowLocations(flowLocations);
		factoryBean.afterPropertiesSet();
		FlowDefinitionRegistry registry = factoryBean.getObject();
		FlowDefinition def = registry.getFlowDefinition("flow");
		assertNotNull(def);
		assertEquals("flow", def.getId());
		assertTrue(def.getAttributes().isEmpty());
	}

	@Test
	public void testGetFlowRegistryCustomFlowServices() throws Exception {
		FlowLocation location1 = new FlowLocation(null, "org/springframework/webflow/config/flow.xml", null);
		FlowLocation[] flowLocations = new FlowLocation[] { location1 };
		factoryBean.setFlowLocations(flowLocations);
		factoryBean.setFlowBuilderServices(TestFlowBuilderServicesFactory.getServices());
		factoryBean.afterPropertiesSet();
		FlowDefinitionRegistry registry = factoryBean.getObject();
		FlowDefinition def = registry.getFlowDefinition("flow");
		assertNotNull(def);
		assertEquals("flow", def.getId());
		assertTrue(def.getAttributes().isEmpty());
	}
}

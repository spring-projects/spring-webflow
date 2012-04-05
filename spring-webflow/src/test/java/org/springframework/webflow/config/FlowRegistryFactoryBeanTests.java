package org.springframework.webflow.config;

import java.util.HashSet;

import junit.framework.TestCase;

import org.springframework.core.io.Resource;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.model.FlowModel;
import org.springframework.webflow.engine.model.builder.FlowModelBuilderException;
import org.springframework.webflow.engine.model.builder.ResourceBackedFlowModelBuilder;
import org.springframework.webflow.engine.model.registry.FlowModelLocator;
import org.springframework.webflow.test.TestFlowBuilderServicesFactory;

public class FlowRegistryFactoryBeanTests extends TestCase {
	private FlowRegistryFactoryBean factoryBean;

	public void setUp() {
		factoryBean = new FlowRegistryFactoryBean();
	}

	public void tearDown() throws Exception {
		factoryBean.destroy();
	}

	public void testGetFlowRegistry() throws Exception {
		HashSet<FlowElementAttribute> attributes = new HashSet<FlowElementAttribute>();
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

	/*
	 * Mock implementation for subsequent tests
	 */
	public static class MockFlowModelBuilder implements ResourceBackedFlowModelBuilder {

		private Resource resource;

		public void init() throws FlowModelBuilderException {
		}

		public void dispose() throws FlowModelBuilderException {
		}

		public void build() throws FlowModelBuilderException {
		}

		public void setFlowModelLocator(FlowModelLocator modelLocator) {
		}

		public FlowModel getFlowModel() throws FlowModelBuilderException {
			return new FlowModel();
		}

		public void setFlowResource(Resource resource) {
			this.resource = resource;
		}

		public Resource getFlowModelResource() {
			return resource;
		}

		public boolean hasFlowModelResourceChanged() {
			return false;
		}
	}

	public void testUsesConfiguredFlowModelBuilderForLocations() throws Exception {
		FlowLocation location1 = new FlowLocation(null, "org/springframework/webflow/config/flow.lmx", null);
		FlowLocation[] flowLocations = new FlowLocation[] { location1 };
		factoryBean.setFlowLocations(flowLocations);
		factoryBean.setFlowBuilderServices(TestFlowBuilderServicesFactory.getServices());
		FlowModelBuilderInfo[] flowBuilders = new FlowModelBuilderInfo[] { new FlowModelBuilderInfo("lmx",
				"org.springframework.webflow.config.FlowRegistryFactoryBeanTests.MockFlowModelBuilder") };
		factoryBean.setFlowModelBuilders(flowBuilders);
		factoryBean.afterPropertiesSet();
		FlowDefinitionRegistry bean = factoryBean.getObject();
		assertTrue(bean.containsFlowDefinition("flow"));
	}

	public void testThrowsExceptionIfExtensionNotFound() throws Exception {
		FlowLocation location1 = new FlowLocation(null, "org/springframework/webflow/config/flow-alt.xxx", null);
		FlowLocation[] flowLocations = new FlowLocation[] { location1 };
		factoryBean.setFlowLocations(flowLocations);
		factoryBean.setFlowBuilderServices(TestFlowBuilderServicesFactory.getServices());
		try {
			factoryBean.afterPropertiesSet();
		} catch (IllegalArgumentException iae) {
			assertEquals("flow-alt.xxx is not a supported resource type; supported types are [xml]", iae.getMessage());
			return;
		}
		fail("Expected IllegalArgumentException");
	}

	public void testUsesConfiguredFlowModelBuilderForLocationPatterns() throws Exception {
		String[] flowLocationPatterns = { "org/springframework/webflow/config/*.lmx" };
		factoryBean.setFlowLocationPatterns(flowLocationPatterns);
		factoryBean.setFlowBuilderServices(TestFlowBuilderServicesFactory.getServices());
		FlowModelBuilderInfo[] flowBuilders = new FlowModelBuilderInfo[] { new FlowModelBuilderInfo("lmx",
				"org.springframework.webflow.config.FlowRegistryFactoryBeanTests.MockFlowModelBuilder") };
		factoryBean.setFlowModelBuilders(flowBuilders);
		factoryBean.afterPropertiesSet();
		FlowDefinitionRegistry bean = factoryBean.getObject();
		assertTrue(bean.containsFlowDefinition("flow"));
	}
}

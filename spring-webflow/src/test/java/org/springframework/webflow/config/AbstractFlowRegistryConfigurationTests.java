package org.springframework.webflow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionConstructionException;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.NoSuchFlowDefinitionException;

public abstract class AbstractFlowRegistryConfigurationTests {

	protected ApplicationContext context;

	protected FlowDefinitionRegistry registry;

	@BeforeEach
	public void setUp() {
		this.context = initApplicationContext();
		this.registry = (FlowDefinitionRegistry) context.getBean("flowRegistry");
	}

	protected abstract ApplicationContext initApplicationContext();


	@Test
	public void testRegistryFlowLocationsPopulated() {
		FlowDefinition flow = registry.getFlowDefinition("flow");
		assertEquals("flow", flow.getId());
		assertEquals("bar", flow.getAttributes().get("foo"));
		assertEquals(2, flow.getAttributes().get("bar"));
	}

	@Test
	public void testRegistryFlowLocationPatternsPopulated() {
		FlowDefinition flow1 = registry.getFlowDefinition("flow1");
		assertEquals("flow1", flow1.getId());
		FlowDefinition flow2 = registry.getFlowDefinition("flow2");
		assertEquals("flow2", flow2.getId());
	}

	@Test
	public void testRegistryFlowBuildersPopulated() {
		FlowDefinition foo = registry.getFlowDefinition("foo");
		assertEquals("foo", foo.getId());
	}

	@Test
	public void testRegistryFlowBuildersPopulatedWithId() {
		FlowDefinition foo = registry.getFlowDefinition("foo2");
		assertEquals("foo2", foo.getId());
	}

	@Test
	public void testRegistryFlowBuildersPopulatedWithAttributes() {
		FlowDefinition foo3 = registry.getFlowDefinition("foo3");
		assertEquals("foo3", foo3.getId());
		assertEquals("bar", foo3.getAttributes().get("foo"));
		assertEquals(2, foo3.getAttributes().get("bar"));
	}

	@Test
	public void testNoSuchFlow() {
		try {
			registry.getFlowDefinition("not there");
		} catch (NoSuchFlowDefinitionException e) {

		}
	}

	@Test
	public void testBogusPath() {
		try {
			registry.getFlowDefinition("bogus");
			fail("Should have failed");
		} catch (FlowDefinitionConstructionException e) {
		}
	}

	@Test
	public void testParent() {
		assertNotNull(registry.getParent());
		assertEquals("parentFlow", registry.getParent().getFlowDefinition("parentFlow").getId());
	}

}

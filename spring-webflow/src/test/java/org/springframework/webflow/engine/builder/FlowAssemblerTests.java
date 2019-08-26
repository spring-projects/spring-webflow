package org.springframework.webflow.engine.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.test.MockFlowBuilderContext;

public class FlowAssemblerTests {
	private FlowBuilder builder;
	private FlowAssembler assembler;
	private FlowBuilderContext builderContext;

	@BeforeEach
	public void setUp() {
		builder = EasyMock.createMock(FlowBuilder.class);
		builderContext = new MockFlowBuilderContext("search");
		assembler = new FlowAssembler(builder, builderContext);
	}

	@Test
	public void testAssembleFlow() {
		builder.init(builderContext);
		builder.dispose();
		builder.buildVariables();
		builder.buildInputMapper();
		builder.buildStartActions();
		builder.buildStates();
		builder.buildGlobalTransitions();
		builder.buildEndActions();
		builder.buildOutputMapper();
		builder.buildExceptionHandlers();
		EasyMock.expect(builder.getFlow()).andReturn(new Flow("search"));
		EasyMock.replay(builder);
		Flow flow = assembler.assembleFlow();
		assertEquals("search", flow.getId());
		EasyMock.verify(builder);
	}

	@Test
	public void testDisposeCalledOnException() {
		builder.init(builderContext);
		EasyMock.expectLastCall().andThrow(new IllegalArgumentException());
		builder.dispose();
		EasyMock.replay(builder);
		try {
			assembler.assembleFlow();
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			EasyMock.verify(builder);
		}
	}
}

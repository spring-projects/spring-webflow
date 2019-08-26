package org.springframework.webflow.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.test.GeneratedFlowExecutionKey;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowExecutionKey;

public class FlowExecutorImplTests {
	private FlowExecutor flowExecutor;

	// mocks
	private FlowDefinitionLocator locator;
	private FlowDefinition definition;
	private FlowExecutionFactory factory;
	private FlowExecution execution;
	private FlowExecutionRepository repository;
	private FlowExecutionLock lock;

	@BeforeEach
	public void setUp() {
		locator = EasyMock.createMock(FlowDefinitionLocator.class);
		definition = EasyMock.createMock(FlowDefinition.class);
		factory = EasyMock.createMock(FlowExecutionFactory.class);
		execution = EasyMock.createMock(FlowExecution.class);
		repository = EasyMock.createMock(FlowExecutionRepository.class);
		lock = EasyMock.createMock(FlowExecutionLock.class);

		flowExecutor = new FlowExecutorImpl(locator, factory, repository);
	}

	@Test
	public void testLaunchFlowExecution() {
		String flowId = "foo";
		MutableAttributeMap<Object> input = null;
		MockExternalContext context = new MockExternalContext();

		EasyMock.expect(locator.getFlowDefinition(flowId)).andReturn(definition);
		EasyMock.expect(factory.createFlowExecution(definition)).andReturn(execution);

		execution.start(input, context);

		execution.hasEnded();
		EasyMock.expectLastCall().andReturn(false);

		MockFlowExecutionKey flowExecutionKey = new MockFlowExecutionKey("12345");
		EasyMock.expect(execution.getKey()).andReturn(flowExecutionKey);
		EasyMock.expect(repository.getLock(flowExecutionKey)).andReturn(lock);

		lock.lock();

		repository.putFlowExecution(execution);

		lock.unlock();

		EasyMock.expect(execution.getDefinition()).andReturn(definition);
		EasyMock.expect(definition.getId()).andReturn("foo");
		EasyMock.expect(execution.getKey()).andReturn(flowExecutionKey);

		replayMocks();

		FlowExecutionResult result = flowExecutor.launchExecution("foo", null, context);
		assertTrue(result.isPaused());
		assertEquals("12345", result.getPausedKey());
		assertFalse(result.isEnded());
		assertNull(result.getOutcome());
		assertNull(ExternalContextHolder.getExternalContext());
		verifyMocks();
	}

	@Test
	public void testLaunchFlowExecutionEndsAfterProcessing() {
		String flowId = "foo";
		MutableAttributeMap<Object> input = null;
		MockExternalContext context = new MockExternalContext();

		EasyMock.expect(locator.getFlowDefinition(flowId)).andReturn(definition);
		EasyMock.expect(factory.createFlowExecution(definition)).andReturn(execution);

		execution.start(input, context);

		execution.hasEnded();
		EasyMock.expectLastCall().andReturn(true);

		EasyMock.expect(execution.getDefinition()).andReturn(definition);
		EasyMock.expect(definition.getId()).andReturn("foo");
		EasyMock.expect(execution.getOutcome()).andReturn(new FlowExecutionOutcome("finish", null));

		replayMocks();

		FlowExecutionResult result = flowExecutor.launchExecution("foo", null, context);
		assertTrue(result.isEnded());
		assertEquals("finish", result.getOutcome().getId());
		assertTrue(result.getOutcome().getOutput().isEmpty());
		assertFalse(result.isPaused());
		assertNull(result.getPausedKey());
		assertNull(ExternalContextHolder.getExternalContext());
		verifyMocks();
	}

	@Test
	public void testResumeFlowExecution() {
		String flowExecutionKey = "12345";
		MockExternalContext context = new MockExternalContext();
		FlowExecutionKey key = new GeneratedFlowExecutionKey();

		EasyMock.expect(repository.parseFlowExecutionKey(flowExecutionKey)).andReturn(key);
		EasyMock.expect(repository.getLock(key)).andReturn(lock);

		lock.lock();
		EasyMock.expect(repository.getFlowExecution(key)).andReturn(execution);

		execution.resume(context);

		execution.hasEnded();
		EasyMock.expectLastCall().andReturn(false);

		repository.putFlowExecution(execution);

		EasyMock.expect(execution.getDefinition()).andReturn(definition);
		EasyMock.expect(definition.getId()).andReturn("foo");
		EasyMock.expect(execution.getKey()).andReturn(new MockFlowExecutionKey("12345"));

		lock.unlock();

		replayMocks();
		FlowExecutionResult result = flowExecutor.resumeExecution(flowExecutionKey, context);
		verifyMocks();

		assertTrue(result.isPaused());
		assertEquals("12345", result.getPausedKey());
		assertFalse(result.isEnded());
		assertNull(result.getOutcome());
		assertNull(ExternalContextHolder.getExternalContext());
		verifyMocks();

	}

	@Test
	public void testResumeFlowExecutionEndsAfterProcessing() {
		String flowExecutionKey = "12345";
		MockExternalContext context = new MockExternalContext();
		FlowExecutionKey key = new MockFlowExecutionKey("12345");

		EasyMock.expect(repository.parseFlowExecutionKey(flowExecutionKey)).andReturn(key);
		EasyMock.expect(repository.getLock(key)).andReturn(lock);

		lock.lock();
		EasyMock.expect(repository.getFlowExecution(key)).andReturn(execution);

		execution.resume(context);

		execution.hasEnded();
		EasyMock.expectLastCall().andReturn(true);

		EasyMock.expect(execution.getDefinition()).andReturn(definition);
		EasyMock.expect(definition.getId()).andReturn("foo");

		LocalAttributeMap<Object> output = new LocalAttributeMap<>();
		output.put("foo", "bar");
		EasyMock.expect(execution.getOutcome()).andReturn(new FlowExecutionOutcome("finish", output));

		repository.removeFlowExecution(execution);

		lock.unlock();

		replayMocks();

		FlowExecutionResult result = flowExecutor.resumeExecution(flowExecutionKey, context);
		assertTrue(result.isEnded());
		assertEquals("finish", result.getOutcome().getId());
		assertEquals(output, result.getOutcome().getOutput());
		assertFalse(result.isPaused());
		assertNull(result.getPausedKey());
		assertNull(ExternalContextHolder.getExternalContext());
		verifyMocks();
	}

	private void replayMocks() {
		EasyMock.replay(locator, definition, factory, execution, repository, lock);
	}

	private void verifyMocks() {
		EasyMock.verify(locator, definition, factory, execution, repository, lock);
	}
}

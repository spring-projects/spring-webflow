package org.springframework.webflow.executor;

import junit.framework.TestCase;

import org.easymock.EasyMock;
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

public class FlowExecutorImplTests extends TestCase {
	private FlowExecutor flowExecutor;

	// mocks
	private FlowDefinitionLocator locator;
	private FlowDefinition definition;
	private FlowExecutionFactory factory;
	private FlowExecution execution;
	private FlowExecutionRepository repository;
	private FlowExecutionLock lock;

	protected void setUp() {
		locator = (FlowDefinitionLocator) EasyMock.createMock(FlowDefinitionLocator.class);
		definition = (FlowDefinition) EasyMock.createMock(FlowDefinition.class);
		factory = (FlowExecutionFactory) EasyMock.createMock(FlowExecutionFactory.class);
		execution = (FlowExecution) EasyMock.createMock(FlowExecution.class);
		repository = (FlowExecutionRepository) EasyMock.createMock(FlowExecutionRepository.class);
		lock = (FlowExecutionLock) EasyMock.createMock(FlowExecutionLock.class);

		flowExecutor = new FlowExecutorImpl(locator, factory, repository);
	}

	public void testLaunchFlowExecution() {
		String flowId = "foo";
		MutableAttributeMap input = null;
		MockExternalContext context = new MockExternalContext();

		EasyMock.expect(locator.getFlowDefinition(flowId)).andReturn(definition);
		EasyMock.expect(factory.createFlowExecution(definition)).andReturn(execution);

		execution.start(input, context);

		execution.hasEnded();
		EasyMock.expectLastCall().andReturn(Boolean.FALSE);

		repository.putFlowExecution(execution);

		EasyMock.expect(execution.getDefinition()).andReturn(definition);
		EasyMock.expect(definition.getId()).andReturn("foo");
		EasyMock.expect(execution.getKey()).andReturn(new MockFlowExecutionKey("12345"));

		replayMocks();

		FlowExecutionResult result = flowExecutor.launchExecution("foo", null, context);
		assertTrue(result.isPaused());
		assertEquals("12345", result.getPausedKey());
		assertFalse(result.isEnded());
		assertNull(result.getOutcome());
		assertNull(ExternalContextHolder.getExternalContext());
		verifyMocks();
	}

	public void testLaunchFlowExecutionEndsAfterProcessing() {
		String flowId = "foo";
		MutableAttributeMap input = null;
		MockExternalContext context = new MockExternalContext();

		EasyMock.expect(locator.getFlowDefinition(flowId)).andReturn(definition);
		EasyMock.expect(factory.createFlowExecution(definition)).andReturn(execution);

		execution.start(input, context);

		execution.hasEnded();
		EasyMock.expectLastCall().andReturn(Boolean.TRUE);

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
		EasyMock.expectLastCall().andReturn(Boolean.FALSE);

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
		EasyMock.expectLastCall().andReturn(Boolean.TRUE);

		EasyMock.expect(execution.getDefinition()).andReturn(definition);
		EasyMock.expect(definition.getId()).andReturn("foo");

		LocalAttributeMap output = new LocalAttributeMap();
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
		EasyMock.replay(new Object[] { locator, definition, factory, execution, repository, lock });
	}

	private void verifyMocks() {
		EasyMock.verify(new Object[] { locator, definition, factory, execution, repository, lock });
	}
}

/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;

/**
 * The default implementation of the central facade for <i>driving</i> the execution of flows within an application.
 * <p>
 * This object is responsible for creating and launching new flow executions as requested by clients, as well as
 * resuming existing, paused executions (that were waiting to be resumed in response to a user event).
 * <p>
 * This object is a facade or entry point into the Spring Web Flow execution system and makes the overall system easier
 * to use. The name <i>executor</i> was chosen as <i>executors drive executions</i>.
 * <p>
 * <b>Commonly used configurable properties</b><br>
 * <table border="1">
 * <tr>
 * <td><b>name</b></td>
 * <td><b>description</b></td>
 * <td><b>default</b></td>
 * </tr>
 * <tr>
 * <td>definitionLocator</td>
 * <td>The service locator responsible for loading flow definitions to execute.</td>
 * <td>None</td>
 * </tr>
 * <tr>
 * <td>executionFactory</td>
 * <td>The factory responsible for creating new flow executions.</td>
 * <td>None</td>
 * </tr>
 * <tr>
 * <td>executionRepository</td>
 * <td>The repository responsible for managing flow execution persistence.</td>
 * <td>None</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @see FlowDefinitionLocator
 * @see FlowExecutionFactory
 * @see FlowExecutionRepository
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Colin Sampaleanu
 */
public class FlowExecutorImpl implements FlowExecutor {

	private static final Log logger = LogFactory.getLog(FlowExecutorImpl.class);

	/**
	 * The locator to access flow definitions registered in a central registry.
	 */
	private FlowDefinitionLocator definitionLocator;

	/**
	 * The abstract factory for creating a new execution of a flow definition.
	 */
	private FlowExecutionFactory executionFactory;

	/**
	 * The repository used to save, update, and load existing flow executions to/from a persistent store.
	 */
	private FlowExecutionRepository executionRepository;

	/**
	 * Create a new flow executor.
	 * @param definitionLocator the locator for accessing flow definitions to execute
	 * @param executionFactory the factory for creating executions of flow definitions
	 * @param executionRepository the repository for persisting paused flow executions
	 */
	public FlowExecutorImpl(FlowDefinitionLocator definitionLocator, FlowExecutionFactory executionFactory,
			FlowExecutionRepository executionRepository) {
		Assert.notNull(definitionLocator, "The locator for accessing flow definitions is required");
		Assert.notNull(executionFactory, "The execution factory for creating new flow executions is required");
		Assert.notNull(executionRepository, "The repository for persisting flow executions is required");
		this.definitionLocator = definitionLocator;
		this.executionFactory = executionFactory;
		this.executionRepository = executionRepository;
	}

	/**
	 * Returns the locator to load flow definitions to execute.
	 */
	public FlowDefinitionLocator getDefinitionLocator() {
		return definitionLocator;
	}

	/**
	 * Returns the abstract factory used to create new executions of a flow.
	 */
	public FlowExecutionFactory getExecutionFactory() {
		return executionFactory;
	}

	/**
	 * Returns the repository used to save, update, and load existing flow executions to/from a persistent store.
	 */
	public FlowExecutionRepository getExecutionRepository() {
		return executionRepository;
	}

	public FlowExecutionResult launchExecution(String flowId, MutableAttributeMap input, ExternalContext context)
			throws FlowException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Launching new execution of flow '" + flowId + "' with input " + input);
			}
			ExternalContextHolder.setExternalContext(context);
			FlowDefinition flowDefinition = definitionLocator.getFlowDefinition(flowId);
			FlowExecution flowExecution = executionFactory.createFlowExecution(flowDefinition);
			flowExecution.start(input, context);
			if (!flowExecution.hasEnded()) {
				executionRepository.putFlowExecution(flowExecution);
				return createPausedResult(flowExecution);
			} else {
				return createEndResult(flowExecution);
			}
		} finally {
			ExternalContextHolder.setExternalContext(null);
		}
	}

	public FlowExecutionResult resumeExecution(String flowExecutionKey, ExternalContext context) throws FlowException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Resuming flow execution with key '" + flowExecutionKey);
			}
			ExternalContextHolder.setExternalContext(context);
			FlowExecutionKey key = executionRepository.parseFlowExecutionKey(flowExecutionKey);
			FlowExecutionLock lock = executionRepository.getLock(key);
			lock.lock();
			try {
				FlowExecution flowExecution = executionRepository.getFlowExecution(key);
				flowExecution.resume(context);
				if (!flowExecution.hasEnded()) {
					executionRepository.putFlowExecution(flowExecution);
					return createPausedResult(flowExecution);
				} else {
					executionRepository.removeFlowExecution(flowExecution);
					return createEndResult(flowExecution);
				}
			} finally {
				lock.unlock();
			}
		} finally {
			ExternalContextHolder.setExternalContext(null);
		}
	}

	private FlowExecutionResult createEndResult(FlowExecution flowExecution) {
		return FlowExecutionResult.createEndedResult(flowExecution.getDefinition().getId(), flowExecution.getOutcome());
	}

	private FlowExecutionResult createPausedResult(FlowExecution flowExecution) {
		return FlowExecutionResult.createPausedResult(flowExecution.getDefinition().getId(), flowExecution.getKey()
				.toString());
	}

}
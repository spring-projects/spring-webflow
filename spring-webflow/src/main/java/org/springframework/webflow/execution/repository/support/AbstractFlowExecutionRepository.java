/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.execution.repository.support;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;

/**
 * Abstract base class for flow execution repository implementations. Does not
 * make any assumptions about the storage medium used to store active flow
 * executions. Mandates the use of a {@link FlowExecutionStateRestorer}, used
 * to rehydrate a flow execution after it has been obtained from storage
 * from resume.
 * <p>
 * The configured {@link FlowExecutionStateRestorer} should be compatible
 * with the chosen {@link FlowExecution} implementation and is configuration
 * as done by a {@link FlowExecutionFactory} (listeners, execution attributes, ...).
 * 
 * @author Erwin Vervaet
 */
public abstract class AbstractFlowExecutionRepository implements FlowExecutionRepository {

	/**
	 * The strategy for restoring transient flow execution state after
	 * obtaining it from storage.
	 */
	private FlowExecutionStateRestorer executionStateRestorer;
	
	/**
	 * Constructor for use in subclasses.
	 * @param executionStateRestorer the transient flow execution state restorer
	 */
	protected AbstractFlowExecutionRepository(FlowExecutionStateRestorer executionStateRestorer) {
		setExecutionStateRestorer(executionStateRestorer);
	}

	/**
	 * Returns the strategy for restoring transient flow execution state after
	 * obtaining it from storage.
	 * @return the transient flow execution state restorer
	 */
	protected FlowExecutionStateRestorer getExecutionStateRestorer() {
		return executionStateRestorer;
	}
	
	/**
	 * Sets the strategy for restoring transient flow execution state after
	 * obtaining it from storage.
	 * @param executionStateRestorer the transient flow execution state restorer,
	 * may not be null
	 */
	private void setExecutionStateRestorer(
			FlowExecutionStateRestorer executionStateRestorer) {
		Assert.notNull(executionStateRestorer, "The flow execution state restorer is required");
		this.executionStateRestorer = executionStateRestorer;
	}

}

/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.executor.support;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.executor.FlowExecutor;

/**
 * A helper strategy used by the {@link FlowRequestHandler} to extract
 * {@link FlowExecutor} method arguments from a request initiated by
 * an {@link ExternalContext}. The extracted arguments were typically
 * exposed in the previous response (the response that resulted in 
 * a new request) using a {@link FlowExecutorArgumentExposer}.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowExecutorArgumentExtractor {

	/**
	 * Returns true if the flow id is extractable from the context.
	 * @param context the context in which a external user event occured
	 * @return true if extractable, false if not
	 */
	public boolean isFlowIdPresent(ExternalContext context);

	/**
	 * Extracts the flow id from the external context.
	 * @param context the context in which a external user event occured
	 * @return the extracted flow id
	 * @throws FlowExecutorArgumentExtractionException if the flow id could not
	 * be extracted
	 */
	public String extractFlowId(ExternalContext context) throws FlowExecutorArgumentExtractionException;

	/**
	 * Returns true if the flow execution key is extractable from the context.
	 * @param context the context in which a external user event occured
	 * @return true if extractable, false if not
	 */
	public boolean isFlowExecutionKeyPresent(ExternalContext context);

	/**
	 * Extract the flow execution key from the external context.
	 * @param context the context in which the external user event occured
	 * @return the obtained flow execution key
	 * @throws FlowExecutorArgumentExtractionException if the flow execution key
	 * could not be extracted
	 */
	public String extractFlowExecutionKey(ExternalContext context) throws FlowExecutorArgumentExtractionException;

	/**
	 * Returns true if the event id is extractable from the context.
	 * @param context the context in which a external user event occured
	 * @return true if extractable, false if not
	 */
	public boolean isEventIdPresent(ExternalContext context);

	/**
	 * Extract the flow execution event id from the external context.
	 * <p>
	 * This method should only be called if a {@link FlowExecutionKey} was
	 * successfully extracted, indicating a request to resume a flow execution.
	 * @param context the context in which a external user event occured
	 * @return the event id
	 * @throws FlowExecutorArgumentExtractionException if the event id could not
	 * be extracted
	 */
	public String extractEventId(ExternalContext context) throws FlowExecutorArgumentExtractionException;

}
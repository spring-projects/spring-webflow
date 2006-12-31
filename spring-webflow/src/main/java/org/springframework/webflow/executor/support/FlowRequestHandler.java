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
package org.springframework.webflow.executor.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.ResponseInstruction;

/**
 * An immutable helper for flow controllers that encapsulates reusable workflow
 * required to launch and resume flow executions using a {@link FlowExecutor}.
 * <p>
 * The {@link #handleFlowRequest(ExternalContext)} method is the central helper
 * operation and implements the following algorithm:
 * <ol>
 * <li>Extract the flow execution id by calling
 * {@link FlowExecutorArgumentExtractor#extractFlowExecutionKey(ExternalContext)}.
 * <li>If a valid flow execution id was extracted, signal an event in that
 * existing execution to resume it. The event to signal is determined by calling
 * the {@link FlowExecutorArgumentExtractor#extractEventId(ExternalContext)}
 * method. If no event can be extracted, the existing execution will be refreshed.
 * <li>If no flow execution id was extracted, launch a new flow execution. The
 * top-level flow definition for which an execution is created is determined by
 * extracting the flow id using the method
 * {@link FlowExecutorArgumentExtractor#extractFlowId(ExternalContext)}. If no
 * valid flow id can be determined, an exception is thrown.
 * </ol>
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class FlowRequestHandler {

	/**
	 * Logger.
	 */
	private static final Log logger = LogFactory.getLog(FlowRequestHandler.class);

	/**
	 * The flow executor this helper will coordinate with.
	 */
	private FlowExecutor flowExecutor;

	/**
	 * A helper for extracting arguments of flow executor operations
	 * from the external context.
	 */
	private FlowExecutorArgumentExtractor argumentExtractor;

	/**
	 * Creates a new flow controller helper. Will use the default
	 * {@link RequestParameterFlowExecutorArgumentHandler}.
	 * @param flowExecutor the flow execution manager to delegate to
	 */
	public FlowRequestHandler(FlowExecutor flowExecutor) {
		this(flowExecutor, new RequestParameterFlowExecutorArgumentHandler());
	}

	/**
	 * Creates a new flow controller helper.
	 * @param flowExecutor the flow executor to delegate to
	 * @param argumentExtractor the flow executor argument extractor to use
	 */
	public FlowRequestHandler(FlowExecutor flowExecutor, FlowExecutorArgumentExtractor argumentExtractor) {
		Assert.notNull(flowExecutor, "The flow executor is required");
		Assert.notNull(argumentExtractor, "The flow executor argument extractor is required");
		this.flowExecutor = flowExecutor;
		this.argumentExtractor = argumentExtractor;
	}
	
	/**
	 * Returns the flow executor used by this helper.
	 */
	public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}

	/**
	 * Returns the flow executor argument extractor used by this helper.
	 */
	public FlowExecutorArgumentExtractor getArgumentExtractor() {
		return argumentExtractor;
	}

	/**
	 * Handle a request into the Spring Web Flow system from an external system.
	 * @param context the external context in which the request occured
	 * @return the selected view that should be rendered as a response
	 */
	public ResponseInstruction handleFlowRequest(ExternalContext context) throws FlowException {
		if (logger.isDebugEnabled()) {
			logger.debug("Request initiated by " + context);
		}
		if (argumentExtractor.isFlowExecutionKeyPresent(context)) {
			String flowExecutionKey = argumentExtractor.extractFlowExecutionKey(context);
			if (argumentExtractor.isEventIdPresent(context)) {
				String eventId = argumentExtractor.extractEventId(context);
				ResponseInstruction response = flowExecutor.resume(flowExecutionKey, eventId, context);
				if (logger.isDebugEnabled()) {
					logger.debug("Returning [resume] " + response);
				}
				return response;
			}
			else {
				ResponseInstruction response = flowExecutor.refresh(flowExecutionKey, context);
				if (logger.isDebugEnabled()) {
					logger.debug("Returning [refresh] " + response);
				}
				return response;
			}
		}
		else {
			String flowDefinitionId = argumentExtractor.extractFlowId(context);
			ResponseInstruction response = flowExecutor.launch(flowDefinitionId, context);
			if (logger.isDebugEnabled()) {
				logger.debug("Returning [launch] " + response);
			}
			return response;
		}
	}
}
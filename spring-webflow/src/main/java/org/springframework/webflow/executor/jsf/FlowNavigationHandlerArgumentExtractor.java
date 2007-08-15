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
package org.springframework.webflow.executor.jsf;

import org.springframework.util.StringUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.executor.support.FlowExecutorArgumentExtractionException;
import org.springframework.webflow.executor.support.FlowExecutorArgumentExtractor;

/**
 * An {@link FlowExecutorArgumentExtractor} that is aware of JSF outcomes that communicate requests to launch flow
 * executions and signal event in existing flow executions. Designed to be used wih a {@link FlowNavigationHandler}.
 * 
 * Note: this class only implements flow id and event id extraction methods. A FlowNavigationHandler is not expected to
 * extract a flow execution key, as flow execution restoration is fully handled by the {@link FlowPhaseListener} and the
 * JSF restore view phase.
 * 
 * @author Keith Donald
 */
public class FlowNavigationHandlerArgumentExtractor implements FlowExecutorArgumentExtractor {

	/**
	 * The default prefix of a JSF outcome string that indicates a new flow should be launched.
	 */
	private static final String FLOW_ID_PREFIX = "flowId:";

	/**
	 * The prefix for JSF outcome strings indicating a new flow should be launched.
	 */
	private String flowIdPrefix = FLOW_ID_PREFIX;

	/**
	 * Returns the configured prefix for outcome strings that indicate a new flow should be launched.
	 */
	public String getFlowIdPrefix() {
		return flowIdPrefix;
	}

	/**
	 * Sets the prefix of an outcome string that indicates a new flow should be launched.
	 */
	public void setFlowIdPrefix(String flowIdPrefix) {
		this.flowIdPrefix = flowIdPrefix;
	}

	public boolean isFlowIdPresent(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String outcome = getOutcome(context);
		if (outcome != null && outcome.startsWith(getFlowIdPrefix())) {
			return true;
		} else {
			return false;
		}
	}

	public String extractFlowId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		// extract the flowId from a JSF outcome in format <code>${flowIdPrefix}${flowId}</code>
		String outcome = getOutcome(context);
		int index = outcome.indexOf(getFlowIdPrefix());
		if (index == -1) {
			throw new FlowExecutorArgumentExtractionException(
					"Unable to extract flow id; make sure the JSF outcome is prefixed with '" + getFlowIdPrefix()
							+ "' to launch a new flow execution");
		}
		String flowId = outcome.substring(getFlowIdPrefix().length());
		if (!StringUtils.hasText(flowId)) {
			throw new FlowExecutorArgumentExtractionException(
					"Unable to extract flow id; make sure the flow id is provided in the outcome string");
		}
		return flowId;
	}

	public boolean isEventIdPresent(ExternalContext context) {
		return StringUtils.hasText(getOutcome(context));
	}

	public String extractEventId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		// treat the action outcome string as the event id
		return getOutcome(context);
	}

	public boolean isFlowExecutionKeyPresent(ExternalContext context) {
		throw new UnsupportedOperationException("Should not be called by a FlowNavigationHandler");
	}

	public String extractFlowExecutionKey(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		throw new UnsupportedOperationException("Should not be called by a FlowNavigationHandler");
	}

	// helpers
	private String getOutcome(ExternalContext context) {
		return ((JsfExternalContext) context).getOutcome();
	}
}
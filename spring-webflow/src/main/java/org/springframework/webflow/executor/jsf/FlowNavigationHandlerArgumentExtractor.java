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
import org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler;

/**
 * An {@link FlowExecutorArgumentExtractor} that is aware of JSF
 * outcomes that communicate requests to launch flow executions and
 * signal event in existing flow executions.
 * 
 * @author Keith Donald
 */
public class FlowNavigationHandlerArgumentExtractor extends RequestParameterFlowExecutorArgumentHandler {
	
	/*
	 * Implementation note: subclasses an FlowExecutorArgumentHandler but is really
	 * just a FlowExecutorArgumentExtractor.
	 */

	/**
	 * The default prefix of a outcome string that indicates a new flow should be launched.
	 */
	private static final String FLOW_ID_PREFIX = "flowId:";

	private String flowIdPrefix = FLOW_ID_PREFIX;

	/**
	 * Returns the configured prefix for outcome strings that indicate a new flow should be launched.
	 */
	public String getFlowIdPrefix() {
		return flowIdPrefix;
	}

	/**
	 * Sets the prefix of a outcome string that indicates a new flow should be launched.
	 */
	public void setFlowIdPrefix(String flowIdPrefix) {
		this.flowIdPrefix = flowIdPrefix;
	}

	public boolean isEventIdPresent(ExternalContext context) {
		return StringUtils.hasText(getOutcome(context)) || super.isEventIdPresent(context);
	}

	// overidden to return the eventId from the action outcome string.
	public String extractEventId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String outcome = getOutcome(context);
		if (StringUtils.hasText(outcome)) {
			return outcome;
		}
		else {
			return super.extractEventId(context);
		}
	}

	public boolean isFlowIdPresent(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String outcome = getOutcome(context);
		if (outcome != null && outcome.startsWith(getFlowIdPrefix())) {
			return true;
		}
		else {
			return super.isFlowIdPresent(context);
		}
	}

	// overidden to return the flowId from a JSF outcome in format <code>flowId:${flowId}</code>
	public String extractFlowId(ExternalContext context) throws FlowExecutorArgumentExtractionException {
		String outcome = getOutcome(context);
		if (StringUtils.hasText(outcome)) {
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
		else {
			return super.extractFlowId(context);
		}
	}

	private String getOutcome(ExternalContext context) {
		return ((JsfExternalContext)context).getOutcome();
	}
}
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
package org.springframework.webflow.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.style.ToStringCreator;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;

/**
 * Trivial flow handler base class that simply returns null for all operations. Subclasses should extend and override
 * which operations they need.
 * 
 * @author Keith Donald
 */
public class AbstractFlowHandler implements FlowHandler {

	public String getFlowId() {
		return null;
	}

	public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
		return null;
	}

	public String handleExecutionOutcome(FlowExecutionOutcome outcome, HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

	public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	public String toString() {
		return new ToStringCreator(this).append("flowId", getFlowId()).toString();
	}

}

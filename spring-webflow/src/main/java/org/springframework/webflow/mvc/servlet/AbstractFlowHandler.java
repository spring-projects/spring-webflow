package org.springframework.webflow.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;

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

	public String handleExecutionOutcome(String outcome, AttributeMap output, HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

	public String handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

}

package org.springframework.webflow.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;

public interface FlowHandler {
	public String getFlowId();

	public MutableAttributeMap createExecutionInputMap(HttpServletRequest request);

	public ModelAndView handleExecutionOutcome(String outcome, AttributeMap output, HttpServletRequest request,
			HttpServletResponse response);

	public ModelAndView handleException(FlowException e, HttpServletRequest request, HttpServletResponse response);
}

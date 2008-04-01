package org.springframework.webflow.mvc.portlet;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
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

	public MutableAttributeMap createExecutionInputMap(PortletRequest request) {
		return null;
	}

	public String getFlowId() {
		return null;
	}

	public ModelAndView handleException(FlowException e, RenderRequest request, RenderResponse response) {
		return null;
	}

	public ModelAndView handleFlowOutcome(String outcome, AttributeMap output, RenderRequest request,
			RenderResponse response) {
		return null;
	}

}

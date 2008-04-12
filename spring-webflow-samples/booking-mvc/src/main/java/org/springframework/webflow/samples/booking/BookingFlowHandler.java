package org.springframework.webflow.samples.booking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

public class BookingFlowHandler extends AbstractFlowHandler {
    public String handleExecutionOutcome(String outcome, AttributeMap output, HttpServletRequest request,
	    HttpServletResponse response) {
	return "hotels/index";
    }

    @Override
    public ModelAndView handleException(FlowException e, HttpServletRequest request, HttpServletResponse response) {
	return new ModelAndView("redirect:/spring/hotels/index");
    }

}

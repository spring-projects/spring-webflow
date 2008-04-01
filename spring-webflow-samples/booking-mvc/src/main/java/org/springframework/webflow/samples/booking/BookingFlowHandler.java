package org.springframework.webflow.samples.booking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

public class BookingFlowHandler extends AbstractFlowHandler {
    public ModelAndView handleExecutionOutcome(String outcome, AttributeMap output, HttpServletRequest request,
	    HttpServletResponse response) {
	return new ModelAndView(new RedirectView("/spring/hotels/index", true));
    }
}

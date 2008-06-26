package org.springframework.samples.springtravel.hotel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;
import org.springframework.webflow.mvc.servlet.MvcExternalContext;

public class HotelFlowHandlerAdapter extends FlowHandlerAdapter {

	@Override
	protected boolean isContextRequired() {
		// the context will not be a WebApplicationContext in an OSGi environment
		return false;
	}

	@Override
	protected ServletExternalContext createServletExternalContext(HttpServletRequest request, HttpServletResponse response) {
		ServletExternalContext context = new MvcExternalContext(request.getSession().getServletContext(), request, response, getFlowUrlHandler());
		context.setAjaxRequest(getAjaxHandler().isAjaxRequest(request, response));
		return context;
	}
}

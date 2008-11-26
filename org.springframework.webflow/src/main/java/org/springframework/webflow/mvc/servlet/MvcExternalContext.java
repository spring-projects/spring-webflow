package org.springframework.webflow.mvc.servlet;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.webflow.context.servlet.FlowUrlHandler;
import org.springframework.webflow.context.servlet.ServletExternalContext;

/**
 * Spring MVC external context implementation. Is a {@link ServletExternalContext}, but overrides operations to plug in
 * specific functionality available in a Spring MVC Environment.
 * 
 * @author Keith Donald
 */
public class MvcExternalContext extends ServletExternalContext {

	/**
	 * Create a new external context wrapping given servlet HTTP request and response and given servlet context.
	 * @param context the servlet context
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param flowUrlHandler the flow url handler
	 */
	public MvcExternalContext(ServletContext context, HttpServletRequest request, HttpServletResponse response,
			FlowUrlHandler flowUrlHandler) {
		super(context, request, response, flowUrlHandler);
	}

	public Locale getLocale() {
		return RequestContextUtils.getLocale(getRequest());
	}

}

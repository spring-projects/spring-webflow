package org.springframework.webflow.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.executor.FlowExecutor;

public class SpringWebServlet implements Servlet {

	private ServletConfig config;

	private ConfigurableWebApplicationContext container;

	private FlowExecutor flowExecutor;

	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		container = new XmlWebApplicationContext();
		container.setConfigLocations(getConfigLocations(config));
		container.setServletConfig(config);
		container.setServletContext(config.getServletContext());
		container.refresh();
		this.flowExecutor = getFlowExecutor(container);
	}

	public ServletConfig getServletConfig() {
		return config;
	}

	public String getServletInfo() {
		return "Spring Web Servlet";
	}

	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		ServletContext servletContext = getServletConfig().getServletContext();
		new ServletExternalContext(servletContext, request, response).executeFlowRequest(flowExecutor);
	}

	public void destroy() {
		container.close();
	}

	private String[] getConfigLocations(ServletConfig config) {
		String configLocations = config.getInitParameter("configLocations");
		if (configLocations != null) {
			return StringUtils.tokenizeToStringArray(config.getInitParameter("configLocations"),
					ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS);
		} else {
			return new String[] { "/WEB-INF/webapp-config.xml" };
		}
	}

	private FlowExecutor getFlowExecutor(WebApplicationContext container) {
		String[] beanNames = container.getBeanNamesForType(FlowExecutor.class);
		if (beanNames.length == 0) {
			throw new IllegalStateException("No bean of type FlowExecutor defined in context");
		} else if (beanNames.length > 1) {
			throw new IllegalStateException("More than one bean of type FlowExecutor defined in context.");
		} else {
			return (FlowExecutor) container.getBean(beanNames[0]);
		}
	}
}

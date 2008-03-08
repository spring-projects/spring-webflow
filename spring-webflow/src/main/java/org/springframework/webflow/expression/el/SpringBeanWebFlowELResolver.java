package org.springframework.webflow.expression.el;

import javax.el.ELContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.el.SpringBeanELResolver;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * EL resolver for Spring Beans accessible to the flow's local bean factory.
 * @author Jeremy Grelle
 */
public class SpringBeanWebFlowELResolver extends SpringBeanELResolver {

	private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

	private RequestContext context;

	public SpringBeanWebFlowELResolver() {
	}

	public SpringBeanWebFlowELResolver(RequestContext context) {
		this.context = context;
	}

	protected BeanFactory getBeanFactory(ELContext elContext) {
		RequestContext requestContext = getRequestContext();
		if (context != null && requestContext.getActiveFlow().getBeanFactory() != null) {
			BeanFactory factory = requestContext.getActiveFlow().getBeanFactory();
			return factory;
		} else {
			return EMPTY_BEAN_FACTORY;
		}
	}

	protected RequestContext getRequestContext() {
		return context != null ? context : RequestContextHolder.getRequestContext();
	}

}

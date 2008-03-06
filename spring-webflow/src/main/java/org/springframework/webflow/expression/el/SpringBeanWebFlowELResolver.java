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

	protected BeanFactory getBeanFactory(ELContext elContext) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		if (rc != null && rc.getActiveFlow().getBeanFactory() != null) {
			return rc.getActiveFlow().getBeanFactory();
		} else {
			return EMPTY_BEAN_FACTORY;
		}
	}

}

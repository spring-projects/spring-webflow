package org.springframework.faces.webflow;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.web.jsf.SpringBeanVariableResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * JSF 1.1 variable resolver for Spring Beans accessible to the flow's local bean factory.
 * @author Jeremy Grelle
 */
public class SpringBeanWebFlowVariableResolver extends SpringBeanVariableResolver {

	private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

	public SpringBeanWebFlowVariableResolver(VariableResolver originalVariableResolver) {
		super(originalVariableResolver);
	}

	protected BeanFactory getBeanFactory(FacesContext facesContext) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		if (requestContext != null && requestContext.getActiveFlow().getBeanFactory() != null) {
			BeanFactory factory = requestContext.getActiveFlow().getBeanFactory();
			return factory;
		} else {
			return EMPTY_BEAN_FACTORY;
		}
	}

}

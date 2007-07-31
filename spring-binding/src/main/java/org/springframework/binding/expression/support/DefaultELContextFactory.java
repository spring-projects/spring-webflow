package org.springframework.binding.expression.support;

import javax.el.ELContext;

public class DefaultELContextFactory implements ELContextFactory {

    /**
     * Configures and returns a {@link DelegatingELContext} to be used in evaluating EL expressions on the given base
     * target object.
     * 
     * @return DelegatingELContext The configured DelegatingELContext instance.
     */
    public ELContext getELContext(Object target) {
	DelegatingELContext context = new DelegatingELContext();
	DefaultELResolver baseResolver = new DefaultELResolver();
	baseResolver.setTarget(target);
	context.setELResolver(baseResolver);
	return context;
    }

}

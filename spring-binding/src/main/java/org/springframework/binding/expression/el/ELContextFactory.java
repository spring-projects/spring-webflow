package org.springframework.binding.expression.el;

import javax.el.ELContext;

public interface ELContextFactory {

    /**
     * Configures and returns a {@link DelegatingELContext} to be used in evaluating EL expressions on the given base
     * target object.
     * 
     * @return DelegatingELContext The configured DelegatingELContext instance.
     */
    public ELContext getELContext(Object target);

}
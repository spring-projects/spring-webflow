package org.springframework.binding.expression.el;

import javax.el.ELContext;

/**
 * A factory for creating a EL context object that will be used to evaluate a target object of an EL expression.
 * 
 * @author Jeremy Grelle
 */
public interface ELContextFactory {

    /**
     * Configures and returns a {@link DelegatingELContext} to be used in evaluating EL expressions on the given base
     * target object.
     * 
     * @return DelegatingELContext The configured DelegatingELContext instance.
     */
    public ELContext getELContext(Object target);

}
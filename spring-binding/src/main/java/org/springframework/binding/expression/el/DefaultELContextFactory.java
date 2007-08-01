package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

/**
 * A default {@link ELContextFactory} for facilitating use of EL for expression evaluation.
 * @author Jeremy Grelle
 * 
 */
public class DefaultELContextFactory implements ELContextFactory {

    /**
     * Configures and returns a simple EL context to use to parse EL expressions.
     * @return The configured simple ELContext instance.
     */
    public ELContext getParseTimeELContext() {
	return new SimpleELContext();
    }

    /**
     * Configures and returns a simple EL context to use to evaluate EL expressions on the given base target object.
     * @return The configured simple ELContext instance.
     */
    public ELContext getEvalTimeELContext(Object target) {
	return new SimpleELContext(target);
    }

    private static class SimpleELContext extends ELContext {
	private DefaultELResolver resolver = new DefaultELResolver();

	public SimpleELContext() {

	}

	public SimpleELContext(Object target) {
	    this.resolver.setTarget(target);
	}

	public ELResolver getELResolver() {
	    return resolver;
	}

	public FunctionMapper getFunctionMapper() {
	    return null;
	}

	public VariableMapper getVariableMapper() {
	    return null;
	}
    }
}
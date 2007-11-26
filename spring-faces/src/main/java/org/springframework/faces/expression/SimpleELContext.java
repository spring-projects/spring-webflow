package org.springframework.faces.expression;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

class SimpleELContext extends ELContext {

	private ELResolver resolver;

	public SimpleELContext(ELResolver resolver) {
		this.resolver = resolver;
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

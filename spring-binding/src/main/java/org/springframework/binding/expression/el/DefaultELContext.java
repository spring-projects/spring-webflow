package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

public class DefaultELContext extends ELContext {

	private VariableMapper variableMapper;

	private ELResolver resolver;

	private FunctionMapper functionMapper;

	public DefaultELContext(ELResolver resolver, VariableMapper variableMapper, FunctionMapper functionMapper) {
		this.resolver = resolver;
		this.variableMapper = variableMapper;
		this.functionMapper = functionMapper;
	}

	public static ELContext createDefaultELContext() {
		return new DefaultELContext(new DefaultELResolver(null, null), null, null);
	}

	public ELResolver getELResolver() {
		return resolver;
	}

	public VariableMapper getVariableMapper() {
		return variableMapper;
	}

	public FunctionMapper getFunctionMapper() {
		return functionMapper;
	}

}

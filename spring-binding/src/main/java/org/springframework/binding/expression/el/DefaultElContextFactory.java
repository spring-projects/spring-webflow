package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.VariableMapper;

public class DefaultElContextFactory implements ELContextFactory {
	public ELContext getELContext(Object target, VariableMapper variableMapper) {
		return new DefaultELContext(new DefaultELResolver(target, null), variableMapper, null);
	}
}

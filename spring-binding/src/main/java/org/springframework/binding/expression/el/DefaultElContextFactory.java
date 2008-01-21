package org.springframework.binding.expression.el;

import javax.el.ELContext;

public class DefaultElContextFactory implements ELContextFactory {
	public ELContext getELContext(Object target) {
		return new DefaultELContext(new DefaultELResolver(target, null), null, null);
	}
}

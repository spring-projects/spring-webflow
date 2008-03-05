package org.springframework.faces.expression;

import javax.el.CompositeELResolver;
import javax.faces.el.PropertyResolver;

import org.springframework.binding.expression.el.MapAdaptableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;

/**
 * Assembles {@link RequestContextELResolver} and {@link ScopeSearchingELResolver} into a composite that may be used
 * with JSF 1.1 and higher for property resolution.
 * 
 * @author Jeremy Grelle
 */
public class CompositeFlowPropertyResolver extends ELDelegatingPropertyResolver {

	private static final CompositeELResolver composite = new CompositeELResolver();

	static {
		composite.add(new RequestContextELResolver());
		composite.add(new ScopeSearchingELResolver());
		composite.add(new MapAdaptableELResolver());
	}

	public CompositeFlowPropertyResolver(PropertyResolver nextResolver) {
		super(nextResolver, composite);
	}
}

package org.springframework.faces.expression;

import javax.el.CompositeELResolver;
import javax.faces.el.VariableResolver;

import org.springframework.binding.expression.el.MapAdaptableELResolver;
import org.springframework.webflow.expression.el.ImplicitFlowVariableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;

/**
 * Assembles {@link RequestContextELResolver} and {@link ScopeSearchingELResolver} into a composite that may be used
 * with JSF 1.1 and higher for variable resolution.
 * 
 * @author Jeremy Grelle
 */
public class CompositeFlowVariableResolver extends ELDelegatingVariableResolver {

	private static final CompositeELResolver composite = new CompositeELResolver();

	static {
		composite.add(new RequestContextELResolver());
		composite.add(new ImplicitFlowVariableELResolver());
		composite.add(new ScopeSearchingELResolver());
		composite.add(new MapAdaptableELResolver());
	}

	public CompositeFlowVariableResolver(VariableResolver nextResolver) {
		super(nextResolver, composite);
	}
}

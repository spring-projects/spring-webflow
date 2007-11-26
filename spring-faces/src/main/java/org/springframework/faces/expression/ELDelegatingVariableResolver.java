package org.springframework.faces.expression;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

public abstract class ELDelegatingVariableResolver extends VariableResolver {

	private VariableResolver nextResolver;

	private ELContext elContext;

	public ELDelegatingVariableResolver(VariableResolver nextResolver, ELResolver delegate) {
		this.nextResolver = nextResolver;
		this.elContext = new SimpleELContext(delegate);
	}

	public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException {
		Object result = elContext.getELResolver().getValue(elContext, null, name);
		if (elContext.isPropertyResolved()) {
			return result;
		} else {
			return nextResolver.resolveVariable(facesContext, name);
		}
	}
}

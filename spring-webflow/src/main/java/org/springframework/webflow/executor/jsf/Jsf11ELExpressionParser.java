package org.springframework.webflow.executor.jsf;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.faces.context.FacesContext;

import org.springframework.binding.expression.el.DefaultELContextFactory;
import org.springframework.binding.expression.el.ELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;

/**
 * A JSF-aware ExpressionParser that allows JSF 1.1 managed beans to be referenced in expressions in the FlowDefinition.
 * @author Jeremy Grelle
 * 
 */
public class Jsf11ELExpressionParser extends ELExpressionParser {

    public Jsf11ELExpressionParser(ExpressionFactory expressionFactory) {
	super(expressionFactory, new Jsf11ELContextFactory());
    }

    public Jsf11ELExpressionParser(ExpressionFactory expressionFactory, ELContextFactory contextFactory) {
	super(expressionFactory, contextFactory);
    }

    private static class Jsf11ELContextFactory extends DefaultELContextFactory {

	public ELContext getEvalTimeELContext(Object target) {

	    FacesContext context = FacesContext.getCurrentInstance();
	    return new Jsf11ELContext(context);
	}

	private static class Jsf11ELContext extends ELContext {

	    ELResolver baseResolver;

	    public Jsf11ELContext(FacesContext context) {
		baseResolver = new Jsf11ELResolverAdapter(context);
	    }

	    public ELResolver getELResolver() {
		return baseResolver;
	    }

	    public FunctionMapper getFunctionMapper() {
		return null;
	    }

	    public VariableMapper getVariableMapper() {
		return null;
	    }
	}
    }

}

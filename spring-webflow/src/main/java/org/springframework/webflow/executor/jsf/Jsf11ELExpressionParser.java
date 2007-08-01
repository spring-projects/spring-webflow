package org.springframework.webflow.executor.jsf;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.faces.context.FacesContext;

import org.springframework.binding.expression.el.ELContextFactory;
import org.springframework.binding.expression.el.JBossELExpressionParser;

/**
 * A JSF-aware ExpressionParser that allows JSF 1.1 managed beans to be referenced in expressions in the FlowDefinition.
 * @author Jeremy Grelle
 * 
 */
public class Jsf11ELExpressionParser extends JBossELExpressionParser {

    public Jsf11ELExpressionParser() {
	super(new Jsf11ELContextFactory());
    }

    public Jsf11ELExpressionParser(ELContextFactory contextFactory) {
	super(contextFactory);
    }

    private static class Jsf11ELContextFactory implements ELContextFactory {

	public ELContext getELContext(Object target) {

	    FacesContext context = FacesContext.getCurrentInstance();
	    return new Jsf11ELContext(context);
	}

	private static class Jsf11ELContext extends ELContext {

	    ELResolver baseResolver;

	    public Jsf11ELContext(FacesContext context) {
		baseResolver = new ELResolverAdapter(context);
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

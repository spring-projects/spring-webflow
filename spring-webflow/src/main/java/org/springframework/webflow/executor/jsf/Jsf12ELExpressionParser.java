package org.springframework.webflow.executor.jsf;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.springframework.binding.expression.el.ELContextFactory;
import org.springframework.binding.expression.el.JBossELExpressionParser;

/**
 * A JSF-aware ExpressionParser that allows JSF 1.2 managed beans to be referenced in expressions in the FlowDefinition.
 * @author Jeremy Grelle
 * 
 */
public class Jsf12ELExpressionParser extends JBossELExpressionParser {

    public Jsf12ELExpressionParser() {
	super(new Jsf11ELContextFactory());
    }

    public Jsf12ELExpressionParser(ELContextFactory contextFactory) {
	super(contextFactory);
    }

    private static class Jsf11ELContextFactory implements ELContextFactory {

	public ELContext getELContext(Object target) {

	    FacesContext context = FacesContext.getCurrentInstance();
	    if (context != null) {
		return context.getELContext();
	    }
	    return null;
	}
    }

}

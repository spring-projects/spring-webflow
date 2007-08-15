package org.springframework.webflow.executor.jsf;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;

import org.springframework.binding.expression.el.DefaultELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;

/**
 * A JSF-aware ExpressionParser that allows JSF 1.2 managed beans to be referenced in expressions in the FlowDefinition.
 * @author Jeremy Grelle
 * 
 */
public class Jsf12ELExpressionParser extends ELExpressionParser {

	public Jsf12ELExpressionParser(ExpressionFactory expressionFactory) {
		super(expressionFactory, new Jsf12ELContextFactory());
	}

	private static class Jsf12ELContextFactory extends DefaultELContextFactory {
		public ELContext getEvaluationContext(Object target) {
			return FacesContext.getCurrentInstance().getELContext();
		}
	}

}

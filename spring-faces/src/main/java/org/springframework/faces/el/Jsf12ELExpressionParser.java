package org.springframework.faces.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;

import org.springframework.binding.expression.el.DefaultELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;

/**
 * A JSF-aware ExpressionParser that allows JSF 1.2 managed beans to be referenced in expressions in the FlowDefinition.
 * @author Jeremy Grelle
 */
public class Jsf12ELExpressionParser extends ELExpressionParser {

	/**
	 * Creates a JSF 1.2 expression parser
	 * @param expressionFactory the unified EL expression factory implementation to use
	 */
	public Jsf12ELExpressionParser(ExpressionFactory expressionFactory) {
		super(expressionFactory, new Jsf12ELContextFactory());
	}

	/**
	 * Simple little helper that grabs the current EL context from the faces context to support EL expression
	 * evaluation.
	 */
	private static class Jsf12ELContextFactory extends DefaultELContextFactory {
		public ELContext getEvaluationContext(Object target) {
			return FacesContext.getCurrentInstance().getELContext();
		}
	}

}

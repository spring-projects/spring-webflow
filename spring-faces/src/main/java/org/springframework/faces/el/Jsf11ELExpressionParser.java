package org.springframework.faces.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.faces.context.FacesContext;

import org.springframework.binding.expression.el.DefaultELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;

/**
 * A JSF-aware ExpressionParser that allows JSF 1.1 managed beans to be referenced in expressions in the FlowDefinition.
 * @author Jeremy Grelle
 */
public class Jsf11ELExpressionParser extends ELExpressionParser {

	/**
	 * Creates a new JSF 1.1 compatible expression parser
	 * @param expressionFactory the unified EL expression factory implementation
	 */
	public Jsf11ELExpressionParser(ExpressionFactory expressionFactory) {
		super(expressionFactory, new Jsf11ELContextFactory());
	}

	/**
	 * Inner helper class that plus in the EL Resolver that resolves expressions using JSF 1.1 APIs.
	 */
	private static class Jsf11ELContextFactory extends DefaultELContextFactory {

		public ELContext getEvaluationContext(Object target) {
			return new Jsf11ELContext(FacesContext.getCurrentInstance());
		}

		private static class Jsf11ELContext extends ELContext {

			private ELResolver baseResolver;

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

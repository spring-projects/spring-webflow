package org.springframework.binding.expression.el;

import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserException;

/**
 * An expression parser that parses EL expressions.
 * @author Jeremy Grelle
 */
public class ELExpressionParser implements ExpressionParser {

	/**
	 * The expression prefix.
	 */
	private static final String EXPRESSION_PREFIX = "#{";

	/**
	 * The expression suffix.
	 */
	private static final String EXPRESSION_SUFFIX = "}";

	/**
	 * The ExpressionFactory for constructing EL expressions
	 */
	private ExpressionFactory expressionFactory;

	private Map contextFactories = new HashMap();

	/**
	 * Creates a new EL expression parser for standalone usage.
	 */
	public ELExpressionParser(ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
	}

	/**
	 * Register the ELContextFactory for expressions that evaluate the given class of target object.
	 * @param expressionTargetType the expression target class
	 * @param contextFactory the context factory to use for expressions that evaluate those types of targets
	 */
	public void putContextFactory(Class expressionTargetType, ELContextFactory contextFactory) {
		this.contextFactories.put(expressionTargetType, contextFactory);
	}

	public boolean isEvalExpressionString(String expressionString) {
		return expressionString.startsWith(EXPRESSION_PREFIX) && expressionString.endsWith(EXPRESSION_SUFFIX);
	}

	public Expression parseExpression(String expressionString, Class expressionTargetType,
			Class expectedEvaluationResultType, ExpressionVariable[] expressionVariables) throws ParserException {
		if (expectedEvaluationResultType == null) {
			throw new ParserException(expressionString, "The 'expectedEvaluationResultType' argument is required; "
					+ "specify Object.class if the type is unknown", new NullPointerException());
		}
		try {
			ParserELContext context = new ParserELContext();
			context.mapVariables(expressionVariables, expressionFactory);
			ValueExpression expression = expressionFactory.createValueExpression(context, expressionString,
					expectedEvaluationResultType);
			ELContextFactory contextFactory = getContextFactory(expressionTargetType, expressionString);
			return new ELExpression(contextFactory, expression, context.getVariableMapper());
		} catch (ELException ex) {
			throw new ParserException(expressionString, ex);
		}
	}

	private ELContextFactory getContextFactory(Class expressionTargetType, String expressionString) {
		if (!contextFactories.containsKey(expressionTargetType)) {
			throw new ParserException(expressionString, new IllegalArgumentException(
					"No ELContextFactory registered for expressionTargetType [" + expressionTargetType + "]; "
							+ "Please ensure a factory is registered for this type."));
		}
		return (ELContextFactory) contextFactories.get(expressionTargetType);
	}

	private static class ParserELContext extends ELContext {
		private VariableMapper variableMapper;

		public ELResolver getELResolver() {
			return null;
		}

		public FunctionMapper getFunctionMapper() {
			return null;
		}

		public VariableMapper getVariableMapper() {
			return variableMapper;
		}

		public void mapVariables(ExpressionVariable[] variables, ExpressionFactory expressionFactory) {
			if (variables != null && variables.length > 0) {
				variableMapper = new VariableMapperImpl();
				for (int i = 0; i < variables.length; i++) {
					ExpressionVariable var = variables[i];
					ValueExpression expr = expressionFactory.createValueExpression(this, var.getValue(), Object.class);
					variableMapper.setVariable(var.getName(), expr);
				}
			}
		}
	}

	private static class VariableMapperImpl extends VariableMapper {
		private Map variables = new HashMap();

		public ValueExpression resolveVariable(String name) {
			return (ValueExpression) variables.get(name);
		}

		public ValueExpression setVariable(String name, ValueExpression value) {
			return (ValueExpression) variables.put(name, value);
		}

		public String toString() {
			return variables.toString();
		}
	}

}

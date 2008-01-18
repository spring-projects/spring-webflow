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
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.support.NullParserContext;
import org.springframework.util.Assert;

/**
 * An expression parser that parses EL expressions.
 * @author Jeremy Grelle
 * @author Keith Donald
 */
public class ELExpressionParser implements ExpressionParser {

	/**
	 * The expression prefix.
	 */
	private static final String EXPRESSION_PREFIX_IMMEDIATE = "${";

	/**
	 * The expression prefix.
	 */
	private static final String EXPRESSION_PREFIX_DEFERRED = "#{";

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
		init(expressionFactory);
	}

	/**
	 * Register the ELContextFactory for expressions that evaluate the given class of context object.
	 * @param contextType the expression context class
	 * @param contextFactory the context factory to use for expressions that evaluate those types of contexts
	 */
	public void putContextFactory(Class contextType, ELContextFactory contextFactory) {
		Assert.notNull(contextFactory, "The EL context factory cannot be null");
		contextFactories.put(contextType, contextFactory);
	}

	public boolean isDelimitedExpression(String expressionString) {
		return (expressionString.startsWith(EXPRESSION_PREFIX_DEFERRED) && expressionString.endsWith(EXPRESSION_SUFFIX))
				|| (expressionString.startsWith(EXPRESSION_PREFIX_IMMEDIATE) && expressionString
						.endsWith(EXPRESSION_SUFFIX));
	}

	public Expression parseExpression(String expressionString, ParserContext context) throws ParserException {
		if (context == null) {
			context = NullParserContext.INSTANCE;
		}
		try {
			ParserELContext elContext = new ParserELContext();
			elContext.mapVariables(context.getExpressionVariables(), expressionFactory);
			ValueExpression expression = expressionFactory.createValueExpression(elContext, expressionString,
					getExpectedType(context));
			ELContextFactory contextFactory = getContextFactory(context.getEvaluationContextType(), expressionString);
			return new ELExpression(contextFactory, expression, elContext.getVariableMapper());
		} catch (ELException e) {
			throw new ParserException(expressionString, e);
		}
	}

	private Class getExpectedType(ParserContext context) {
		Class expectedType = context.getExpectedEvaluationResultType();
		if (expectedType != null) {
			return expectedType;
		} else {
			return Object.class;
		}
	}

	private ELContextFactory getContextFactory(Class expressionTargetType, String expressionString) {
		if (contextFactories.containsKey(expressionTargetType)) {
			return (ELContextFactory) contextFactories.get(expressionTargetType);
		} else {
			return (ELContextFactory) contextFactories.get(Object.class);
		}
	}

	private void init(ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
		DefaultElContextFactory defaultContextFactory = new DefaultElContextFactory();
		putContextFactory(null, defaultContextFactory);
		putContextFactory(Object.class, defaultContextFactory);
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
					ValueExpression expr = expressionFactory.createValueExpression(this,
							String.valueOf(var.getValue()), Object.class);
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

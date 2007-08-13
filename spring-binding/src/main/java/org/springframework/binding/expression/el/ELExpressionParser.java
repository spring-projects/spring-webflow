package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.SettableExpression;

/**
 * An expression parser that parses EL expressions.
 * @author Jeremy Grelle
 */
public class ELExpressionParser implements ExpressionParser {

	/**
	 * The expression prefix for deferred EL expressions.
	 */
	private static final String DEFERRED_EL_EXPRESSION_PREFIX = "#{";

	/**
	 * The expression suffix for deferred EL expressions.
	 */
	private static final String DEFERRED_EL_EXPRESSION_SUFFIX = "}";

	/**
	 * The marked expression delimiter prefix.
	 */
	private String expressionPrefix = DEFERRED_EL_EXPRESSION_PREFIX;

	/**
	 * The marked expression delimiter suffix.
	 */
	private String expressionSuffix = DEFERRED_EL_EXPRESSION_SUFFIX;

	/**
	 * The {@link ELContextFactory} for retrieving a configured ELContext.
	 */
	private ELContextFactory contextFactory;

	/**
	 * The ExpressionFactory for constructing EL expressions
	 */
	private ExpressionFactory expressionFactory;

	/**
	 * Creates a new EL expression parser for standalone usage.
	 */
	public ELExpressionParser(ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
		this.contextFactory = new DefaultELContextFactory();
	}

	/**
	 * Creates a new EL expression parser with a custom context factory for a specific environment.
	 * 
	 * @param contextFactory the context factory
	 */
	public ELExpressionParser(ExpressionFactory expressionFactory, ELContextFactory contextFactory) {
		this.expressionFactory = expressionFactory;
		this.contextFactory = contextFactory;
	}

	/**
	 * Check whether or not given criteria are expressed as an expression.
	 */
	public boolean isDelimitedExpression(String expressionString) {
		int prefixIndex = expressionString.indexOf(expressionPrefix);
		if (prefixIndex == -1) {
			return false;
		}
		int suffixIndex = expressionString.indexOf(expressionSuffix, prefixIndex);
		if (suffixIndex == -1) {
			return false;
		} else {
			if (suffixIndex == prefixIndex + expressionPrefix.length()) {
				return false;
			} else {
				return true;
			}
		}
	}

	public final Expression parseExpression(String expressionString) throws ParserException {
		if (!isDelimitedExpression(expressionString)) {
			expressionString = expressionPrefix + expressionString + expressionSuffix;
		}
		return doParseExpression(expressionString);
	}

	public final SettableExpression parseSettableExpression(String expressionString) throws ParserException,
			UnsupportedOperationException {
		if (!isDelimitedExpression(expressionString)) {
			expressionString = expressionPrefix + expressionString + expressionSuffix;
		}
		return doParseSettableExpression(expressionString);
	}

	/**
	 * Parses the expression string into an EL value expression.
	 * @param expressionString
	 * @throws ParserException
	 */
	protected Expression doParseExpression(String expressionString) throws ParserException {
		return doParseSettableExpression(expressionString);
	}

	/**
	 * Parses the expression string into an EL value expression.
	 * @param expressionString
	 * @throws ParserException
	 */
	protected SettableExpression doParseSettableExpression(String expressionString) throws ParserException {
		ELContext ctx = contextFactory.getParseContext();
		try {
			return new ELExpression(contextFactory, expressionFactory.createValueExpression(ctx, expressionString,
					Object.class));
		} catch (ELException ex) {
			throw new ParserException(expressionString, ex);
		}
	}
}

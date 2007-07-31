package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.SettableExpression;

/**
 * An expression parser that parses EL expressions. Beyond standard EL expression parsing, it makes use of the ability
 * of the JBoss-EL implementation to parse dynamic method invocations such as foo.bar() (the EL spec currently only
 * provides out-of-the-box support for functions).
 * 
 * @author Jeremy Grelle
 */
public class JBossELExpressionParser implements ExpressionParser {

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
    private ELContextFactory contextFactory = new DefaultELContextFactory();

    /**
     * The ExpressionFactory for constructing EL expressions
     */
    private ExpressionFactory factory = new ExpressionFactoryImpl();

    public String getExpressionPrefix() {
	return expressionPrefix;
    }

    public String getExpressionSuffix() {
	return expressionSuffix;
    }

    /**
     * Check whether or not given criteria are expressed as an expression.
     */
    public boolean isDelimitedExpression(String expressionString) {
	int prefixIndex = expressionString.indexOf(getExpressionPrefix());
	if (prefixIndex == -1) {
	    return false;
	}
	int suffixIndex = expressionString.indexOf(getExpressionSuffix(), prefixIndex);
	if (suffixIndex == -1) {
	    return false;
	} else {
	    if (suffixIndex == prefixIndex + getExpressionPrefix().length()) {
		return false;
	    } else {
		return true;
	    }
	}
    }

    public Expression parseExpression(String expressionString) throws ParserException {
	if (!isDelimitedExpression(expressionString)) {
	    expressionString = getExpressionPrefix() + expressionString + getExpressionSuffix();
	}
	return doParseExpression(expressionString);
    }

    public SettableExpression parseSettableExpression(String expressionString) throws ParserException,
	    UnsupportedOperationException {
	if (!isDelimitedExpression(expressionString)) {
	    expressionString = getExpressionPrefix() + expressionString + getExpressionSuffix();
	}
	return doParseSettableExpression(expressionString);
    }

    /**
     * Parses the expression string into an EL value expression.
     * 
     * @param expressionString
     * @throws ParserException
     */
    protected Expression doParseExpression(String expressionString) throws ParserException {
	return doParseSettableExpression(expressionString);
    }

    /**
     * Parses the expression string into an EL value expression.
     * 
     * @param expressionString
     * @throws ParserException
     */
    protected SettableExpression doParseSettableExpression(String expressionString) throws ParserException {
	ELContext ctx = getELContextFactory().getELContext(null);
	try {
	    return new ELExpression(getELContextFactory(), factory.createValueExpression(ctx, expressionString,
		    Object.class));
	} catch (ELException ex) {
	    throw new ParserException(expressionString, ex);
	}
    }

    /**
     * Returns the proper {@link ELContextFactory} for the current environment.
     * 
     * @return ELContextFactory The ELContextFactory for the current environment.
     */
    protected ELContextFactory getELContextFactory() {
	return contextFactory;
    }
}

package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

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
    private ELContextFactory contextFactory;

    /**
     * The ExpressionFactory for constructing EL expressions
     */
    private ExpressionFactory factory = new ExpressionFactoryImpl();

    /**
     * Creates a new EL expression parser for standalone usage.
     */
    public JBossELExpressionParser() {
	this.contextFactory = new DefaultELContextFactory();
    }

    /**
     * Creates a new EL expression parser with a custom context factory for a specific environment.
     * 
     * @param contextFactory the context factory
     */
    public JBossELExpressionParser(ELContextFactory contextFactory) {
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
	ELContext ctx = contextFactory.getELContext(null);
	try {
	    return new ELExpression(contextFactory, factory.createValueExpression(ctx, expressionString, Object.class));
	} catch (ELException ex) {
	    throw new ParserException(expressionString, ex);
	}
    }

    static class DefaultELContextFactory implements ELContextFactory {

	/**
	 * Configures and returns a simple EL context to use to evaluate EL expressions on the given base target object.
	 * @return The configured simple ELContext instance.
	 */
	public ELContext getELContext(Object target) {
	    return new SimpleELContext(target);
	}

	private static class SimpleELContext extends ELContext {
	    private DefaultELResolver resolver;

	    public SimpleELContext(Object target) {
		this.resolver = new DefaultELResolver();
		this.resolver.setTarget(target);
	    }

	    public ELResolver getELResolver() {
		return resolver;
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

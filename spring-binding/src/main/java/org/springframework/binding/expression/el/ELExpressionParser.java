/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.support.NullParserContext;
import org.springframework.util.Assert;

/**
 * The expression parser that parses EL expressions.
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public class ELExpressionParser implements ExpressionParser {

	private ExpressionFactory expressionFactory;

	private Map<Class<?>, ELContextFactory> contextFactories = new HashMap<Class<?>, ELContextFactory>();

	private ConversionService conversionService = new DefaultConversionService();

	/**
	 * Creates a new EL expression parser for standalone usage.
	 */
	public ELExpressionParser(ExpressionFactory expressionFactory) {
		init(expressionFactory);
	}

	/**
	 * The conversion service to use to perform type conversions as needed by the Unified EL system. If not specified,
	 * the default is an instance of {@link DefaultConversionService}.
	 */
	public ConversionService getConversionService() {
		return conversionService;
	}

	/**
	 * Sets the conversion service to use to perform type conversions as needed by the Unified EL system.
	 * @param conversionService the conversion service to use
	 */
	public void setConversionService(ConversionService conversionService) {
		Assert.notNull(conversionService, "The conversion service is required");
		this.conversionService = conversionService;
	}

	/**
	 * Register the ELContextFactory for expressions that evaluate the given class of context object.
	 * @param contextType the expression context class
	 * @param contextFactory the context factory to use for expressions that evaluate those types of contexts
	 */
	public void putContextFactory(Class<?> contextType, ELContextFactory contextFactory) {
		Assert.notNull(contextFactory, "The EL context factory cannot be null");
		contextFactories.put(contextType, contextFactory);
	}

	public Expression parseExpression(String expressionString, ParserContext context) throws ParserException {
		Assert.notNull(expressionString, "The expression string to parse is required");
		if (context == null) {
			context = NullParserContext.INSTANCE;
		}
		if (context.isTemplate()) {
			return parseExpressionInternal(expressionString, context, true);
		} else {
			assertNotDelimited(expressionString);
			assertHasText(expressionString);
			return parseExpressionInternal("#{" + expressionString + "}", context, false);
		}
	}

	private Expression parseExpressionInternal(String expressionString, ParserContext context, boolean template)
			throws ParserException {
		Assert.notNull(expressionString, "The expression string to parse is required");
		try {
			ValueExpression expression = parseValueExpression(expressionString, context);
			ELContextFactory contextFactory = getContextFactory(context.getEvaluationContextType(), expressionString);
			return new ELExpression(contextFactory, expression);
		} catch (ELException e) {
			throw new ParserException(expressionString, e);
		}
	}

	private ValueExpression parseValueExpression(String expressionString, ParserContext context) throws ELException {
		ParserELContext elContext = new ParserELContext();
		elContext.mapVariables(context.getExpressionVariables(), expressionFactory);
		ValueExpression expression = expressionFactory.createValueExpression(elContext, expressionString, Object.class);
		return new BindingValueExpression(expression, getExpectedType(context), conversionService, context.isTemplate());
	}

	private Class<?> getExpectedType(ParserContext context) {
		Class<?> expectedType = context.getExpectedEvaluationResultType();
		return expectedType != null ? expectedType : Object.class;
	}

	private ELContextFactory getContextFactory(Class<?> expressionTargetType, String expressionString) {
		if (contextFactories.containsKey(expressionTargetType)) {
			return contextFactories.get(expressionTargetType);
		} else {
			return contextFactories.get(Object.class);
		}
	}

	private void init(ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
		DefaultElContextFactory contextFactory = new DefaultElContextFactory();
		putContextFactory(null, contextFactory);
		putContextFactory(Object.class, contextFactory);
	}

	private void assertNotDelimited(String expressionString) {
		if ((expressionString.startsWith("#{") && expressionString.endsWith("}"))
				|| (expressionString.startsWith("${") && expressionString.endsWith("}"))) {
			throw new ParserException(expressionString, "This expression '" + expressionString
					+ "' being parsed is expected be an 'eval' EL expression string. "
					+ "Do not attempt to enclose such expression strings in #{} or ${} delimiters. "
					+ "If you need to parse a template that mixes literal text with evaluatable blocks, "
					+ "set the 'template' parser context attribute to true.", null);
		}
	}

	private void assertHasText(String expressionString) {
		if (expressionString.length() == 0) {
			throw new ParserException(expressionString, "The EL eval expression to parse must have text", null);
		}
	}

	private class ParserELContext extends ELContext {
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
				for (ExpressionVariable var : variables) {
					ParserContext context = var.getParserContext() != null ? var.getParserContext()
							: NullParserContext.INSTANCE;
					ValueExpression expr;
					if (context.isTemplate()) {
						expr = parseValueExpression(var.getValueExpression(), context);
					} else {
						assertNotDelimited(var.getValueExpression());
						assertHasText(var.getValueExpression());
						expr = parseValueExpression("#{" + var.getValueExpression() + "}", context);
					}
					variableMapper.setVariable(var.getName(), expr);
				}
			}
		}
	}

	private static class VariableMapperImpl extends VariableMapper {
		private Map<String, ValueExpression> variables = new HashMap<>();

		public ValueExpression resolveVariable(String name) {
			return variables.get(name);
		}

		public ValueExpression setVariable(String name, ValueExpression value) {
			return variables.put(name, value);
		}

		public String toString() {
			return variables.toString();
		}
	}

}

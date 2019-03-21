/*
 * Copyright 2004-2018 the original author or authors.
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
package org.springframework.binding.expression.spel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;
import org.springframework.binding.expression.ParserException;
import org.springframework.binding.expression.support.NullParserContext;
import org.springframework.binding.expression.support.SimpleParserContext;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

/**
 * Adapt the Spring EL {@link SpelExpressionParser} to the Spring Binding
 * {@link ExpressionParser} contract.
 *
 * @author Rossen Stoyanchev
 * @since 2.1.0
 */
public class SpringELExpressionParser implements ExpressionParser {

	private final SpelExpressionParser expressionParser;

	private final ConversionService conversionService;

	private final List<PropertyAccessor> propertyAccessors = new ArrayList<>();

	private final SimpleEvaluationContextFactory simpleContextFactory;


	public SpringELExpressionParser(SpelExpressionParser expressionParser) {
		this(expressionParser, new DefaultConversionService());
	}

	public SpringELExpressionParser(SpelExpressionParser expressionParser, ConversionService conversionService) {
		this.expressionParser = expressionParser;
		this.propertyAccessors.add(new MapAccessor());
		this.conversionService = conversionService;
		this.simpleContextFactory = new SimpleEvaluationContextFactory(this.propertyAccessors, conversionService);
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void addPropertyAccessor(PropertyAccessor propertyAccessor) {
		propertyAccessors.add(propertyAccessor);
	}

	public Expression parseExpression(String expression, ParserContext context) throws ParserException {

		Assert.hasText(expression, "The expression string to parse is required and must not be empty");

		context = (context == null) ? NullParserContext.INSTANCE : context;
		Map<String, Expression> expressionVars = parseSpelExpressionVariables(context.getExpressionVariables());

		org.springframework.expression.Expression spelExpression = parseSpelExpression(expression, context);
		Class<?> expectedResultType = context.getExpectedEvaluationResultType();
		org.springframework.core.convert.ConversionService cs = conversionService.getDelegateConversionService();

		return context instanceof SimpleParserContext ?
				new SpringELExpression(spelExpression, expectedResultType, simpleContextFactory) :
				createSpringELExpression(expressionVars, spelExpression, expectedResultType, cs);
	}

	/**
	 * Create the {@link SpringELExpression}.
	 * <p><strong>Note:</strong> as of 2.4.8, this method is not invoked when a
	 * {@link SimpleParserContext} is passed in, which is mainly the case when using
	 * SpEL for data binding. In those scenarios, the configuration options are
	 * limited to the use of property accessors and a ConversionService.
	 */
	protected SpringELExpression createSpringELExpression(Map<String, Expression> expressionVars,
			org.springframework.expression.Expression spelExpression, Class<?> expectedResultType,
			org.springframework.core.convert.ConversionService conversionService) {

		return new SpringELExpression(spelExpression, expressionVars,
				expectedResultType, conversionService, propertyAccessors);
	}

	private org.springframework.expression.Expression parseSpelExpression(String expression, ParserContext context) {
		org.springframework.expression.ParserContext spelParserContext = getSpelParserContext(context);
		if (spelParserContext != null) {
			return expressionParser.parseExpression(expression, spelParserContext);
		}
		return expressionParser.parseExpression(expression);
	}

	private org.springframework.expression.ParserContext getSpelParserContext(ParserContext context) {
		return context.isTemplate() ? org.springframework.expression.ParserContext.TEMPLATE_EXPRESSION : null;
	}

	/**
	 * Turn {@link ExpressionVariable}'s (pairs of variable names and string expressions)
	 * into a map of variable names and parsed Spring EL expressions. The map will be saved
	 * in a Spring EL {@link EvaluationContext} for later use at evaluation time.
	 *
	 * @param expressionVars an array of ExpressionVariable instances.
	 * @return a Map or null if the input array is empty.
	 */
	private Map<String, Expression> parseSpelExpressionVariables(ExpressionVariable[] expressionVars) {
		if (expressionVars == null || expressionVars.length == 0) {
			return null;
		}
		Map<String, Expression> result = new HashMap<>(expressionVars.length);
		for (ExpressionVariable var : expressionVars) {
			result.put(var.getName(), parseExpression(var.getValueExpression(), var.getParserContext()));
		}
		return result;
	}

}

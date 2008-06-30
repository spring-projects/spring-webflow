/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.engine.builder.support;

import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.converters.Converter;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.WildcardTransitionCriteria;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.execution.RequestContext;

/**
 * Converter that takes an encoded string representation and produces a corresponding <code>TransitionCriteria</code>
 * object.
 * <p>
 * This converter supports the following encoded forms:
 * <ul>
 * <li>"*" - will result in a TransitionCriteria object that matches on everything. </li>
 * <li>"eventId" - will result in a TransitionCriteria object that matches given event id. </li>
 * <li>"${...}" - will result in a TransitionCriteria object that evaluates given condition, expressed as an
 * expression. </li>
 * </ul>
 * 
 * @see org.springframework.webflow.engine.TransitionCriteria
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
class TextToTransitionCriteria implements Converter {

	/**
	 * Context for flow builder services.
	 */
	private FlowBuilderContext flowBuilderContext;

	/**
	 * Create a new converter that converts strings to transition criteria objects. Custom transition criteria will be
	 * looked up using given service locator.
	 */
	public TextToTransitionCriteria(FlowBuilderContext flowBuilderContext) {
		this.flowBuilderContext = flowBuilderContext;
	}

	public Class getSourceClass() {
		return String.class;
	}

	public Class getTargetClass() {
		return TransitionCriteria.class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		String encodedCriteria = (String) source;
		ExpressionParser parser = flowBuilderContext.getExpressionParser();
		if (!StringUtils.hasText(encodedCriteria)
				|| WildcardTransitionCriteria.WILDCARD_EVENT_ID.equals(encodedCriteria)) {
			return WildcardTransitionCriteria.INSTANCE;
		} else {
			return createBooleanExpressionTransitionCriteria(encodedCriteria, parser);
		}
	}

	/**
	 * Hook method subclasses can override to return a specialized expression evaluating transition criteria
	 * implementation.
	 * @param encodedCriteria the encoded transition criteria expression
	 * @param parser the parser that should parse the expression
	 * @return the transition criteria object
	 * @throws ConversionExecutionException when something goes wrong
	 */
	protected TransitionCriteria createBooleanExpressionTransitionCriteria(String encodedCriteria,
			ExpressionParser parser) throws ConversionExecutionException {
		Expression expression = parser.parseExpression(encodedCriteria, new FluentParserContext().template().evaluate(
				RequestContext.class));
		return new DefaultTransitionCriteria(expression);
	}
}
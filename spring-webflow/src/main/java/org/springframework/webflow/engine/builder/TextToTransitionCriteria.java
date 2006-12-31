/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.engine.builder;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.support.AbstractConverter;
import org.springframework.binding.expression.Expression;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.WildcardTransitionCriteria;
import org.springframework.webflow.engine.support.BooleanExpressionTransitionCriteria;
import org.springframework.webflow.engine.support.EventIdTransitionCriteria;

/**
 * Converter that takes an encoded string representation and produces a
 * corresponding <code>TransitionCriteria</code> object.
 * <p>
 * This converter supports the following encoded forms:
 * <ul>
 * <li>"*" - will result in a TransitionCriteria object that matches on
 * everything ({@link org.springframework.webflow.engine.WildcardTransitionCriteria})
 * </li>
 * <li>"eventId" - will result in a TransitionCriteria object that matches
 * given event id ({@link org.springframework.webflow.engine.support.EventIdTransitionCriteria})
 * </li>
 * <li>"${...}" - will result in a TransitionCriteria object that evaluates
 * given condition, expressed as an expression
 * ({@link org.springframework.webflow.engine.support.BooleanExpressionTransitionCriteria})
 * </li>
 * <li>"bean:&lt;id&gt;" - will result in usage of a custom TransitionCriteria
 * bean implementation.</li>
 * </ul>
 * 
 * @see org.springframework.webflow.engine.TransitionCriteria
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class TextToTransitionCriteria extends AbstractConverter {

	/**
	 * Prefix used when the user wants to use a custom TransitionCriteria
	 * implementation managed by a bean factory.
	 */
	private static final String BEAN_PREFIX = "bean:";

	/**
	 * Locator to use for loading custom TransitionCriteria beans.
	 */
	private FlowServiceLocator flowServiceLocator;

	/**
	 * Create a new converter that converts strings to transition criteria
	 * objects. Custom transition criteria will be looked up using given
	 * service locator.
	 */
	public TextToTransitionCriteria(FlowServiceLocator flowServiceLocator) {
		this.flowServiceLocator = flowServiceLocator;
	}

	public Class[] getSourceClasses() {
		return new Class[] { String.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { TransitionCriteria.class };
	}

	protected Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception {
		String encodedCriteria = (String)source;
		if (!StringUtils.hasText(encodedCriteria)
				|| WildcardTransitionCriteria.WILDCARD_EVENT_ID.equals(encodedCriteria)) {
			return WildcardTransitionCriteria.INSTANCE;
		}
		else if (flowServiceLocator.getExpressionParser().isDelimitedExpression(encodedCriteria)) {
			Expression expression = flowServiceLocator.getExpressionParser().parseExpression(encodedCriteria);
			return createBooleanExpressionTransitionCriteria(expression);
		}
		else if (encodedCriteria.startsWith(BEAN_PREFIX)) {
			return flowServiceLocator.getTransitionCriteria(encodedCriteria.substring(BEAN_PREFIX.length()));
		}
		else {
			return createEventIdTransitionCriteria(encodedCriteria);
		}
	}

	/**
	 * Hook method subclasses can override to return a specialized eventId
	 * matching transition criteria implementation.
	 * @param eventId the event id to match
	 * @return the transition criteria object
	 * @throws ConversionException when something goes wrong
	 */
	protected TransitionCriteria createEventIdTransitionCriteria(String eventId) throws ConversionException {
		return new EventIdTransitionCriteria(eventId);
	}

	/**
	 * Hook method subclasses can override to return a specialized expression
	 * evaluating transition criteria implementation.
	 * @param expression the expression to evaluate
	 * @return the transition criteria object
	 * @throws ConversionException when something goes wrong
	 */
	protected TransitionCriteria createBooleanExpressionTransitionCriteria(Expression expression)
			throws ConversionException {
		return new BooleanExpressionTransitionCriteria(expression);
	}
}
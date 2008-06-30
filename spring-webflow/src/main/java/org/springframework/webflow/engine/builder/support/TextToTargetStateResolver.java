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

import org.springframework.binding.convert.converters.Converter;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.execution.RequestContext;

/**
 * Converter that takes an encoded string representation and produces a corresponding {@link TargetStateResolver}
 * object.
 * <p>
 * This converter supports the following encoded forms:
 * <ul>
 * <li>"stateId" - will result in a TargetStateResolver that always resolves the same state. </li>
 * <li>"${stateIdExpression} - will result in a TargetStateResolver that resolves the target state by evaluating an
 * expression against the request context. The resolved value can be a target state identifier or a custom
 * TargetStateResolver to delegate to.</li>
 * </ul>
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
class TextToTargetStateResolver implements Converter {

	/**
	 * Context for flow builder services.
	 */
	private FlowBuilderContext flowBuilderContext;

	/**
	 * Create a new converter that converts strings to transition target state resolver objects. The given conversion
	 * service will be used to do all necessary internal conversion (e.g. parsing expression strings).
	 */
	public TextToTargetStateResolver(FlowBuilderContext flowBuilderContext) {
		this.flowBuilderContext = flowBuilderContext;
	}

	public Class getSourceClass() {
		return String.class;
	}

	public Class getTargetClass() {
		return TargetStateResolver.class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		String targetStateId = (String) source;
		if (!StringUtils.hasText(targetStateId)) {
			return null;
		}
		ExpressionParser parser = flowBuilderContext.getExpressionParser();
		Expression expression = parser.parseExpression(targetStateId, new FluentParserContext().template().evaluate(
				RequestContext.class).expectResult(String.class));
		return new DefaultTargetStateResolver(expression);

	}
}
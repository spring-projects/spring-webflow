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
package org.springframework.binding.expression.support;

import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;

/**
 * {@link ParserContext} for use in data binding scenarios, providing
 * {@link org.springframework.binding.expression.ExpressionParser ExpressionParser}
 * implementations with a hint that they can meaningfully restrict the EL feature set.
 * Specifically designed for use with the Spring EL
 * {@link org.springframework.expression.spel.support.SimpleEvaluationContext
 * SimpleEvaluationContext}.
 *
 * @author Rossen Stoyanchev
 * @since 2.4.8
 */
public final class SimpleParserContext implements ParserContext {

	private Class<?> evaluationContextType;


	public SimpleParserContext(Class<?> evaluationContextType) {
		this.evaluationContextType = evaluationContextType;
	}


	public Class<?> getEvaluationContextType() {
		return this.evaluationContextType;
	}

	public Class<?> getExpectedEvaluationResultType() {
		return null;
	}

	public ExpressionVariable[] getExpressionVariables() {
		return null;
	}

	public boolean isTemplate() {
		return false;
	}
}

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
package org.springframework.binding.expression.support;

import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;

/**
 * A null object implementation of ParserContext. Mainly used internally by expression parser implementations when
 * <code>null</code> is passed in as a parser context value.
 * @author Keith Donad
 */
public final class NullParserContext implements ParserContext {

	/**
	 * The null parser context object instance; a singleton.
	 */
	public static final ParserContext INSTANCE = new NullParserContext();

	private NullParserContext() {
	}

	public Class getEvaluationContextType() {
		return null;
	}

	public Class getExpectedEvaluationResultType() {
		return null;
	}

	public ExpressionVariable[] getExpressionVariables() {
		return null;
	}

	public boolean isTemplate() {
		return false;
	}
}
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
package org.springframework.binding.expression;

/**
 * An expression capable of evaluating itself against context objects. Encapsulates the details of a previously parsed
 * expression string. Provides a common abstraction for expression evaluation independent of any language like
 * Spring EL or the Unified EL.
 * 
 * @author Keith Donald
 */
public interface Expression {

	/**
	 * Evaluate this expression in the provided context and return the result of evaluation.
	 * @param context the context to evaluate this expression in
	 * @return the evaluation result
	 * @throws EvaluationException an exception occurred during expression evaluation
	 */
	Object getValue(Object context) throws EvaluationException;

	/**
	 * Set this expression in the provided context to the value provided.
	 * @param context the context on which the new value should be set
	 * @param value the new value to set
	 * @throws EvaluationException an exception occurred during expression evaluation
	 */
	void setValue(Object context, Object value) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(Object, Object)} method for the given
	 * context.
	 * @param context the context to evaluate
	 * @return the most general type of value that can be set on this context, or <code>null</code> if the type
	 * information cannot be determined
	 * @throws EvaluationException an exception occurred during expression evaluation
	 */
	Class<?> getValueType(Object context) throws EvaluationException;

	/**
	 * Returns the original string used to create this expression, unmodified.
	 * @return the original expression string
	 */
	String getExpressionString();

}

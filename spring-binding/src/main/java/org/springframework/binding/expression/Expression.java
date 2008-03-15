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
package org.springframework.binding.expression;

/**
 * Evaluates a single parsed expression on the provided input object in the specified context. This provides a common
 * abstraction for expression evaluation independent of any language like OGNL or Spring's BeanWrapper.
 * 
 * @author Keith Donald
 */
public interface Expression {

	/**
	 * Evaluate this expression in the provided context and return the result of evaluation.
	 * @param context the context to evaluate this expression in
	 * @return the evaluation result
	 * @throws EvaluationException an exception occured during evaluation
	 */
	public Object getValue(Object context) throws EvaluationException;

	/**
	 * Set this expression in the provided context to the value provided.
	 * @param context the context to set this value to
	 * @param value the new value to be set
	 * @throws EvaluationException an exception occurred during evaluation
	 */
	public void setValue(Object context, Object value) throws EvaluationException;

	/**
	 * Returns the most general type that can be passed to the {@link #setValue(Object, Object)} method for the given
	 * context.
	 * @param context the context of expression evaluation
	 * @return the most general type of value that can be set
	 */
	public Class getValueType(Object context);

	/**
	 * Returns the original string used to create this expression, unmodified.
	 * @return the original expression string
	 */
	public String getExpressionString();

}
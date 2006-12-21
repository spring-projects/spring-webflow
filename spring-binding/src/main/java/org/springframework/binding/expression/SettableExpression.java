/*
 * Copyright 2002-2006 the original author or authors.
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
 * An evaluator that is capable of setting a value on a target object at the
 * path defined by this expression.
 * 
 * @author Keith Donald
 */
public interface SettableExpression extends Expression {

	/**
	 * Evaluate this expression against the target object to set its value to
	 * the value provided.
	 * @param target the target object
	 * @param value the new value to be set
	 * @param context the evaluation context
	 * @throws EvaluationException an exception occured during evaluation
	 */
	public void evaluateToSet(Object target, Object value, EvaluationContext context) throws EvaluationException;
}
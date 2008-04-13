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
package org.springframework.binding.expression;

/**
 * Indicates an expression evaluation failed.
 * 
 * @author Keith Donald
 */
public class EvaluationException extends RuntimeException {

	/**
	 * The evaluation attempt that failed. Transient because an EvaluationAttempt is not serializable.
	 */
	private EvaluationAttempt evaluationAttempt;

	/**
	 * Creates a new evaluation exception.
	 * @param evaluationAttempt the evaluation attempt that failed
	 * @param cause the underlying cause of this exception
	 */
	public EvaluationException(EvaluationAttempt evaluationAttempt, Throwable cause) {
		this(evaluationAttempt, evaluationAttempt
				+ " failed - make sure the expression is evaluatable in the context provided", cause);
	}

	/**
	 * Creates a new evaluation exception.
	 * @param evaluationAttempt the evaluation attempt that failed
	 * @param cause the underlying cause of this exception
	 */
	public EvaluationException(EvaluationAttempt evaluationAttempt, String message, Throwable cause) {
		super(message, cause);
		this.evaluationAttempt = evaluationAttempt;
	}

	/**
	 * Returns the evaluation attempt that failed.
	 */
	public EvaluationAttempt getEvaluationAttempt() {
		return evaluationAttempt;
	}
}
/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.config;

import org.springframework.core.enums.StaticLabeledEnum;
import org.springframework.webflow.execution.repository.continuation.ClientContinuationFlowExecutionRepository;
import org.springframework.webflow.execution.repository.continuation.ContinuationFlowExecutionRepository;
import org.springframework.webflow.execution.repository.support.SimpleFlowExecutionRepository;

/**
 * Type-safe enumeration of logical flow execution repository types.
 * 
 * @see org.springframework.webflow.execution.repository.FlowExecutionRepository
 * 
 * @author Keith Donald
 */
public class RepositoryType extends StaticLabeledEnum {

	/**
	 * The 'simple' flow execution repository type.
	 * @see SimpleFlowExecutionRepository
	 */
	public static RepositoryType SIMPLE = new RepositoryType(0, "Simple");

	/**
	 * The 'continuation' flow execution repository type.
	 * @see ContinuationFlowExecutionRepository
	 */
	public static RepositoryType CONTINUATION = new RepositoryType(1, "Continuation");

	/**
	 * The 'client' (continuation) flow execution repository type.
	 * @see ClientContinuationFlowExecutionRepository
	 */
	public static RepositoryType CLIENT = new RepositoryType(2, "Client");

	/**
	 * The 'singleKey' flow execution repository type.
	 * @see SimpleFlowExecutionRepository
	 * @see SimpleFlowExecutionRepository#setAlwaysGenerateNewNextKey(boolean)
	 */
	public static RepositoryType SINGLEKEY = new RepositoryType(3, "Single Key");
	
	/**
	 * Private constructor because this is a typesafe enum!
	 */
	private RepositoryType(int code, String label) {
		super(code, label);
	}
}
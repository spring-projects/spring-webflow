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
package org.springframework.webflow.config;

import org.springframework.core.enums.StaticLabeledEnum;

/**
 * Type-safe enumeration of logical flow execution repository types.
 * 
 * @see org.springframework.webflow.execution.repository.FlowExecutionRepository
 * 
 * @author Keith Donald
 */
public class FlowExecutionRepositoryType extends StaticLabeledEnum {

	/**
	 * The 'simple' flow execution repository type.
	 */
	public static final FlowExecutionRepositoryType SIMPLE = new FlowExecutionRepositoryType(0, "Simple");

	/**
	 * The 'continuation' flow execution repository type.
	 */
	public static final FlowExecutionRepositoryType CONTINUATION = new FlowExecutionRepositoryType(1, "Continuation");

	/**
	 * The 'client' (continuation) flow execution repository type.
	 */
	public static final FlowExecutionRepositoryType CLIENT = new FlowExecutionRepositoryType(2, "Client");

	/**
	 * The 'singleKey' flow execution repository type.
	 */
	public static final FlowExecutionRepositoryType SINGLEKEY = new FlowExecutionRepositoryType(3, "Single Key");

	/**
	 * Private constructor because this is a typesafe enum!
	 */
	private FlowExecutionRepositoryType(int code, String label) {
		super(code, label);
	}
}
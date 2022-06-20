/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.engine.builder;

import org.springframework.webflow.core.FlowException;

/**
 * Exception thrown to indicate a problem while building a flow.
 * 
 * @see FlowBuilder
 * 
 * @author Erwin Vervaet
 */
public class FlowBuilderException extends FlowException {

	/**
	 * Create a new flow builder exception.
	 * @param message descriptive message
	 */
	public FlowBuilderException(String message) {
		super(message);
	}

	/**
	 * Create a new flow builder exception.
	 * @param message descriptive message
	 * @param cause the underlying cause of this exception
	 */
	public FlowBuilderException(String message, Throwable cause) {
		super(message, cause);
	}
}
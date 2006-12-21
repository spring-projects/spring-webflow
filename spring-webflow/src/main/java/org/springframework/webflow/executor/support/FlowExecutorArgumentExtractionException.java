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
package org.springframework.webflow.executor.support;

import org.springframework.webflow.core.FlowException;

/**
 * An exception thrown by a flow executor argument extractor when an
 * argument could not be extracted.
 * 
 * @see org.springframework.webflow.executor.support.FlowExecutorArgumentExtractor
 * 
 * @author Keith Donald
 */
public class FlowExecutorArgumentExtractionException extends FlowException {

	/**
	 * Creates a new argument extraction exception.
	 * @param msg a descriptive message
	 */
	public FlowExecutorArgumentExtractionException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new argument extraction exception.
	 * @param msg a descriptive message
	 * @param cause the cause
	 */
	public FlowExecutorArgumentExtractionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
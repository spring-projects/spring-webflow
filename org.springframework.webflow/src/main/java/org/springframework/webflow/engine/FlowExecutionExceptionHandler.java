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
package org.springframework.webflow.engine;

import org.springframework.webflow.execution.FlowExecutionException;

/**
 * A strategy for handling an exception that occurs at runtime during the execution of a flow definition.
 * 
 * @author Keith Donald
 */
public interface FlowExecutionExceptionHandler {

	/**
	 * Can this handler handle the given exception?
	 * @param exception the exception that occurred
	 * @return true if yes, false if no
	 */
	public boolean canHandle(FlowExecutionException exception);

	/**
	 * Handle the exception in the context of the current request, optionally making an error view selection that should
	 * be rendered.
	 * @param exception the exception that occurred
	 * @param context the execution control context for this request
	 */
	public void handle(FlowExecutionException exception, RequestControlContext context);
}

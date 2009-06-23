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
package org.springframework.webflow.action;

import org.springframework.webflow.action.MultiAction.MethodResolver;
import org.springframework.webflow.execution.RequestContext;

/**
 * Default method resolver used by the MultiAction class. It uses the following algorithm to calculate a method name:
 * <ol>
 * <li>If the currently executing action has a "method" property defined, use the value as method name.</li>
 * <li>Else use the name of the current state of the flow execution as a method name.</li>
 * </ol>
 * 
 * @see org.springframework.webflow.action.MultiAction
 * 
 * @author Erwin Vervaet
 */
public class DefaultMultiActionMethodResolver implements MethodResolver {

	public String resolveMethod(RequestContext context) {
		// implementation note: not using AnnotatedAction.METHOD_ATTRIBUTE since
		// that resides in the engine subsystem
		String method = context.getAttributes().getString("method");
		if (method == null) {
			if (context.getCurrentState() != null) {
				// default to the state id
				method = context.getCurrentState().getId();
			} else {
				throw new IllegalStateException("Unable to resolve action method; no 'method' context attribute set");
			}
		}
		return method;
	}
}
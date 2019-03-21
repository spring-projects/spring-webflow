/*
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.webflow.validation;

import org.springframework.webflow.execution.FlowExecutionException;

/**
 * A strategy for resolving String-based hints to Objects such as validation
 * groups against a JSR-303 provider.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public interface ValidationHintResolver {

	/**
	 * Resolve the given String hints. Implementations may raise a
	 * {@link FlowExecutionException} if a hint cannot be resolved.
	 *
	 * @param model the model object that will be validated using the hints
	 * @param flowId the current flow id
	 * @param stateId the current state id
	 * @param hints the hints to resolve
	 *
	 * @return an array of resolved hints
	 */
	Object[] resolveValidationHints(Object model, String flowId, String stateId, String[] hints);

}

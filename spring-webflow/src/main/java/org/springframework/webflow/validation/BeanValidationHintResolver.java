/*
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.webflow.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * A JSR-303 (Bean Validation) implementation of {@link ValidationHintResolver}
 * that resolves String-based hints to a {@code Class<?>} array.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class BeanValidationHintResolver implements ValidationHintResolver {

	/**
	 * Resolve each hint as a fully qualified class name or the name of an inner
	 * {@code Class} in the model type or the model or its parent types.
	 *
	 * @param model the model object
	 * @param flowId the current flow id
	 * @param stateId the current view state id
	 * @param hints the hints to resolve
	 *
	 * @return the resolved hints or {@code null}
	 * @throws FlowExecutionException if a hint is unresolved
	 *
	 * @see #handleUnresolvedHint(Object, String, String, String)
	 */
	public Class<?>[] resolveValidationHints(Object model, String flowId, String stateId, String[] hints)
			throws FlowExecutionException {

		if (ObjectUtils.isEmpty(hints)) {
			return null;
		}

		List<Class<?>> result = new ArrayList<Class<?>>();
		for (String hint : hints) {
			if (hint.equalsIgnoreCase("Default")) {
				hint = "javax.validation.groups.Default";
			}
			Class<?> resolvedHint = toClass(hint);
			if ((resolvedHint == null) && (model != null)) {
				resolvedHint = findInnerClass(model.getClass(), StringUtils.capitalize(hint));
			}
			if (resolvedHint == null) {
				resolvedHint = handleUnresolvedHint(model, flowId, stateId, hint);
			}
			if (resolvedHint != null) {
				result.add(resolvedHint);
			}
		}

		return result.toArray(new Class<?>[result.size()]);
	}

	private Class<?> toClass(String hint) {
		try {
			return Class.forName(hint);
		}
		catch (ClassNotFoundException e) {
			// Ignore
		}
		return null;
	}

	private Class<?> findInnerClass(Class<?> targetClass, String hint) {
		try {
			return Class.forName(targetClass.getName() + "$" + hint);
		}
		catch (ClassNotFoundException e) {
			Class<?> superClass = targetClass.getSuperclass();
			if (superClass != null) {
				return findInnerClass(superClass, hint);
			}
		}
		return null;
	}

	/**
	 * Invoked when a hint could not be resolved. This implementation raises a
	 * {@link FlowExecutionException}.
	 *
	 * @param model the model object that will be validated using the hints
	 * @param flowId the current flow id
	 * @param stateId the current state id
	 * @param hint the hint
	 * @return the resolved hint
	 *
	 * @throws FlowExecutionException
	 */
	protected Class<?> handleUnresolvedHint(Object model, String flowId, String stateId, String hint)
			throws FlowExecutionException {

		throw new FlowExecutionException(flowId, stateId, "Failed to resolve validation hint [" + hint + "]");
	}

}

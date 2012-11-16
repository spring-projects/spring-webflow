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
 * Default implementation of {@link ValidationHintResolver} that tries to resolve
 * expects each hint to be either a Class name or the name of an inner Class found
 * in the Class of the model or any of its super classes.
 *
 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class DefaultValidationHintResolver implements ValidationHintResolver {

	public Object[] resolveValidationHints(Object model, String flowId, String stateId, String[] hints) {

		hints = ObjectUtils.addObjectToArray(hints, StringUtils.capitalize(stateId));

		List<Object> result = new ArrayList<Object>();
		for (String hint : hints) {
			Class<?> typeHint = toClass(hint);
			if (typeHint == null) {
				typeHint = findInnerClass(model.getClass(), hint);
			}
			if (typeHint != null) {
				result.add(typeHint);
			}
			else {
				handleUnresolvedHint(model, flowId, stateId, hint);
			}
		}

		return result.toArray();
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
	 *
	 * @throws FlowExecutionException
	 */
	protected void handleUnresolvedHint(Object model, String flowId, String stateId, String hint) {
		if (!stateId.equalsIgnoreCase(hint)) {
			throw new FlowExecutionException(flowId, stateId, "Failed to resolve validation hint [" + hint + "]");
		}
	}

}

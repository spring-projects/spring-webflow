/*
 * Copyright 2004-2020 the original author or authors.
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
package org.springframework.webflow.expression.spel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * <p>
 * Spring EL PropertyAccessor that enables use of the following reserved variables in expressions:
 * </p>
 * 
 * <pre>
 * currentUser
 * flowRequestContext
 * resourceBundle
 * </pre>
 * 
 * <p>
 * Note that any property of {@link RequestContext} (e.g. flowScope, requestParameters, etc.) may also be used in
 * expressions. Such properties are already handled by the {@link ReflectivePropertyAccessor}.
 * </p>
 * 
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class FlowVariablePropertyAccessor implements PropertyAccessor {

	private static Map<String, FlowVariableAccessor> variables = new HashMap<>();

	static {
		variables.put("currentUser", () ->
				RequestContextHolder.getRequestContext().getExternalContext().getCurrentUser());
		variables.put("flowRequestContext", RequestContextHolder::getRequestContext);
		variables.put("resourceBundle", () ->
				RequestContextHolder.getRequestContext().getActiveFlow().getApplicationContext());
	}

	public Class<?>[] getSpecificTargetClasses() {
		return null;
	}

	public boolean canRead(EvaluationContext context, Object target, String name) {
		return variables.containsKey(name);
	}

	public TypedValue read(EvaluationContext context, Object target, String name) {
		FlowVariableAccessor var = variables.get(name);
		return new TypedValue(var.getVariable());
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) {
		return false;
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
		throw new AccessException(name + " is a flow reserved word and cannot be set with an expression.");
	}

	private interface FlowVariableAccessor {
		Object getVariable();
	}

}

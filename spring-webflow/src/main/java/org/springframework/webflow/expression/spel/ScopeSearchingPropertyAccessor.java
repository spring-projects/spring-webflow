/*
 * Copyright 2004-2012 the original author or authors.
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

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;

/**
 * Spring EL PropertyAccessor that searches through all Web Flow scopes.
 * 
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class ScopeSearchingPropertyAccessor implements PropertyAccessor {

	public Class<?>[] getSpecificTargetClasses() {
		return new Class[] { RequestContext.class };
	}

	public boolean canRead(EvaluationContext context, Object target, String name) {
		return (findScopeForAttribute((RequestContext) target, name) != null);
	}

	public TypedValue read(EvaluationContext context, Object target, String name) {
		MutableAttributeMap<Object> scope = findScopeForAttribute((RequestContext) target, name);
		return new TypedValue(scope == null ? null : scope.get(name));
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) {
		return (findScopeForAttribute((RequestContext) target, name) != null);
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) {
		MutableAttributeMap<Object> scope = findScopeForAttribute((RequestContext) target, name);
		if (scope != null) {
			scope.put(name, newValue);
		}
	}

	private MutableAttributeMap<Object> findScopeForAttribute(RequestContext requestContext, String name) {
		if (requestContext.getRequestScope().contains(name)) {
			return requestContext.getRequestScope();
		}
		if (requestContext.getFlashScope().contains(name)) {
			return requestContext.getFlashScope();
		}
		if (requestContext.inViewState() && requestContext.getViewScope().contains(name)) {
			return requestContext.getViewScope();
		}
		if (requestContext.getFlowScope().contains(name)) {
			return requestContext.getFlowScope();
		}
		if (requestContext.getConversationScope().contains(name)) {
			return requestContext.getConversationScope();
		}
		return null;
	}

}

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

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.AnnotatedAction;

/**
 * <p>
 * Spring EL Property Accessor that allows invocation of methods against a resolved Web Flow action, typically a
 * {@link MultiAction} in expressions.
 * </p>
 * 
 * @see org.springframework.webflow.action.EvaluateAction
 * 
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class ActionPropertyAccessor implements PropertyAccessor {

	public Class<?>[] getSpecificTargetClasses() {
		return new Class[] { Action.class };
	}

	public boolean canRead(EvaluationContext context, Object target, String name) {
		return true;
	}

	public TypedValue read(EvaluationContext context, Object target, String name) {
		AnnotatedAction annotated = new AnnotatedAction((Action) target);
		annotated.setMethod(name);
		return new TypedValue(annotated);
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) {
		return false;
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
		throw new AccessException("The Action cannot be set with an expression.");
	}

}

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

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * <p>
 * Spring EL PropertyAccessor that resolves messages from the {@link MessageSource} of the active Flow. The message
 * source itself is accessible through the "resourceBundle" variable (see {@link FlowVariablePropertyAccessor}). To
 * access a specific message use its key in one of the following ways:
 * </p>
 * 
 * <pre>
 * resourceBundle.myErrorCode
 * resourceBundle['myErrorCode']
 * </pre>
 * 
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class MessageSourcePropertyAccessor implements PropertyAccessor {

	public Class<?>[] getSpecificTargetClasses() {
		return new Class[] { MessageSource.class };
	}

	public boolean canRead(EvaluationContext context, Object target, String name) {
		return (getMessage(target, name) != null);
	}

	public TypedValue read(EvaluationContext context, Object target, String name) {
		return new TypedValue(getMessage(target, name));
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) {
		return false;
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
		throw new AccessException("The flow MessageSource is not writable.");
	}

	private String getMessage(Object target, String name) {
		return ((MessageSource) target).getMessage(name, null, null, getLocale());
	}

	private Locale getLocale() {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		return (requestContext != null) ? requestContext.getExternalContext().getLocale() : LocaleContextHolder
				.getLocale();
	}

}

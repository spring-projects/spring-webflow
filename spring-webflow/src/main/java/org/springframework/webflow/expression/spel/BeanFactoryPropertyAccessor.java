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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Spring EL PropertyAccessor for reading beans in a {@link org.springframework.beans.factory.BeanFactory}.
 * 
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class BeanFactoryPropertyAccessor implements PropertyAccessor {

	private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

	public Class<?>[] getSpecificTargetClasses() {
		return null;
	}

	public boolean canRead(EvaluationContext context, Object target, String name) {
		return getBeanFactory().containsBean(name);
	}

	public TypedValue read(EvaluationContext context, Object target, String name) {
		return new TypedValue(getBeanFactory().getBean(name));
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) {
		return false;
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
		throw new AccessException("Beans in a BeanFactory are read-only");
	}

	protected BeanFactory getBeanFactory() {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		if (requestContext != null) {
			BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
			if (beanFactory != null) {
				return beanFactory;
			}
		}
		return EMPTY_BEAN_FACTORY;
	}

}

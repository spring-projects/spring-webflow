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
package org.springframework.webflow.expression.el;

import javax.el.ELContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.el.SpringBeanELResolver;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * EL resolver for resolving Spring Beans accessible by a flow's bean factory.
 * @author Jeremy Grelle
 */
public class SpringBeanWebFlowELResolver extends SpringBeanELResolver {

	private static final BeanFactory EMPTY_BEAN_FACTORY = new StaticListableBeanFactory();

	private RequestContext requestContext;

	public SpringBeanWebFlowELResolver() {
	}

	public SpringBeanWebFlowELResolver(RequestContext context) {
		this.requestContext = context;
	}

	protected BeanFactory getBeanFactory(ELContext elContext) {
		RequestContext requestContext = getRequestContext();
		if (requestContext == null) {
			return EMPTY_BEAN_FACTORY;
		}
		BeanFactory beanFactory = requestContext.getActiveFlow().getApplicationContext();
		return beanFactory != null ? beanFactory : EMPTY_BEAN_FACTORY;
	}

	protected RequestContext getRequestContext() {
		return requestContext != null ? requestContext : RequestContextHolder.getRequestContext();
	}

}

/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.expression.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ELResolver;
import jakarta.el.PropertyNotWritableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * EL resolver for resolving Spring Beans accessible by a flow's bean factory.
 * @author Jeremy Grelle
 */
public class SpringBeanWebFlowELResolver extends ELResolver {

	private static final Log logger = LogFactory.getLog(SpringBeanWebFlowELResolver.class);

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

	// ELResolver implementatio...

	@Override
	public Object getValue(ELContext elContext, Object base, Object property) throws ELException {
		if (base == null) {
			String beanName = property.toString();
			BeanFactory bf = getBeanFactory(elContext);
			if (bf.containsBean(beanName)) {
				if (logger.isTraceEnabled()) {
					logger.trace("Successfully resolved variable '" + beanName + "' in Spring BeanFactory");
				}
				elContext.setPropertyResolved(true);
				return bf.getBean(beanName);
			}
		}
		return null;
	}

	@Override
	public Class<?> getType(ELContext elContext, Object base, Object property) throws ELException {
		if (base == null) {
			String beanName = property.toString();
			BeanFactory bf = getBeanFactory(elContext);
			if (bf.containsBean(beanName)) {
				elContext.setPropertyResolved(true);
				return bf.getType(beanName);
			}
		}
		return null;
	}

	@Override
	public void setValue(ELContext elContext, Object base, Object property, Object value) throws ELException {
		if (base == null) {
			String beanName = property.toString();
			BeanFactory bf = getBeanFactory(elContext);
			if (bf.containsBean(beanName)) {
				if (value == bf.getBean(beanName)) {
					// Setting the bean reference to the same value is alright - can simply be ignored...
					elContext.setPropertyResolved(true);
				}
				else {
					throw new PropertyNotWritableException(
							"Variable '" + beanName + "' refers to " +
									"a Spring bean which by definition is not writable");
				}
			}
		}
	}

	@Override
	public boolean isReadOnly(ELContext elContext, Object base, Object property) throws ELException {
		if (base == null) {
			String beanName = property.toString();
			BeanFactory bf = getBeanFactory(elContext);
			if (bf.containsBean(beanName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
		return Object.class;
	}

}

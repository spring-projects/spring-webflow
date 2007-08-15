/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.action;

import java.io.Serializable;

import org.springframework.binding.method.MethodSignature;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.RequestContext;

/**
 * Thin action proxy that delegates to a method on an arbitrary bean. The bean instance is managed locally by this
 * Action in an instance variable.
 * 
 * @author Keith Donald
 */
class LocalBeanInvokingAction extends AbstractBeanInvokingAction implements Serializable {

	/**
	 * The target bean (any POJO) to invoke.
	 */
	private Object bean;

	/**
	 * Creates a bean invoking action that invokes a method on the specified bean. The bean may be a proxy providing a
	 * layer of indirection if necessary.
	 * @param bean the bean to invoke
	 */
	public LocalBeanInvokingAction(MethodSignature methodSignature, Object bean) {
		super(methodSignature);
		Assert.notNull(bean, "The bean to invoke by this action cannot be null");
		this.bean = bean;
	}

	/**
	 * Returns the target bean to invoke methods on.
	 */
	public Object getBean() {
		return bean;
	}

	protected Object getBean(RequestContext context) throws Exception {
		return getBean();
	}
}
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

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.method.MethodInvoker;
import org.springframework.binding.method.MethodSignature;
import org.springframework.util.Assert;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Base class for actions that delegate to methods on beans (POJOs - Plain Old Java Objects). Acts as an adapter that
 * adapts an {@link Object} method to the Spring Web Flow {@link Action} contract.
 * <p>
 * Subclasses are required to implement the {@link #getBean(RequestContext)} method, returning the bean on which a
 * method should be invoked.
 * 
 * @see BeanInvokingActionFactory
 * 
 * @author Keith Donald
 */
public abstract class AbstractBeanInvokingAction extends AbstractAction {

	/**
	 * The signature of the method to invoke on the target bean, capable of resolving the method when used with a
	 * {@link MethodInvoker}. Required.
	 */
	private MethodSignature methodSignature;

	/**
	 * The method invoker that performs the action-&gt;bean method binding, accepting a {@link MethodSignature} and
	 * {@link #getBean(RequestContext) target bean} instance.
	 */
	private MethodInvoker methodInvoker = new MethodInvoker();

	/**
	 * The specification (configuration) for how bean method return values should be exposed to an executing flow that
	 * invokes this action.
	 */
	private ActionResultExposer methodResultExposer;

	/**
	 * The strategy that adapts bean method return values to Event objects.
	 */
	private ResultEventFactory resultEventFactory = new SuccessEventFactory();

	/**
	 * Creates a new bean invoking action.
	 * @param methodSignature the signature of the method to invoke
	 */
	protected AbstractBeanInvokingAction(MethodSignature methodSignature) {
		Assert.notNull(methodSignature, "The signature of the target method to invoke is required");
		this.methodSignature = methodSignature;
	}

	/**
	 * Returns the signature of the method to invoke on the target bean.
	 */
	public MethodSignature getMethodSignature() {
		return methodSignature;
	}

	/**
	 * Returns the configuration for how bean method return values should be exposed to an executing flow that invokes
	 * this action.
	 */
	public ActionResultExposer getMethodResultExposer() {
		return methodResultExposer;
	}

	/**
	 * Configures how bean method return values should be exposed to an executing flow that invokes this action. This is
	 * optional. By default the bean method return values do not get exposed to the executing flow.
	 */
	public void setMethodResultExposer(ActionResultExposer methodResultExposer) {
		this.methodResultExposer = methodResultExposer;
	}

	/**
	 * Returns the event adaption strategy used by this action.
	 */
	protected ResultEventFactory getResultEventFactory() {
		return resultEventFactory;
	}

	/**
	 * Set the bean return value-&gt;event adaption strategy. Defaults to {@link SuccessEventFactory}, so all bean
	 * method return values will be interpreted as "success".
	 */
	public void setResultEventFactory(ResultEventFactory resultEventFactory) {
		this.resultEventFactory = resultEventFactory;
	}

	/**
	 * Set the conversion service to perform type conversion of event parameters to method arguments as neccessary.
	 * Defaults to {@link DefaultConversionService}.
	 */
	public void setConversionService(ConversionService conversionService) {
		methodInvoker.setConversionService(conversionService);
	}

	/**
	 * Returns the bean method invoker helper.
	 */
	protected MethodInvoker getMethodInvoker() {
		return methodInvoker;
	}

	protected Event doExecute(RequestContext context) throws Exception {
		Object bean = getBean(context);
		Object returnValue = getMethodInvoker().invoke(methodSignature, bean, context);
		if (methodResultExposer != null) {
			methodResultExposer.exposeResult(returnValue, context);
		}
		return resultEventFactory.createResultEvent(bean, returnValue, context);
	}

	// subclassing hooks

	/**
	 * Retrieves the bean to invoke a method on. Subclasses need to implement this method.
	 * @param context the flow execution request context
	 * @return the bean on which to invoke methods
	 * @throws Exception when the bean cannot be retreived
	 */
	protected abstract Object getBean(RequestContext context) throws Exception;

}
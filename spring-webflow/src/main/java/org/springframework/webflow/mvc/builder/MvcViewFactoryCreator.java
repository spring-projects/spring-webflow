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
package org.springframework.webflow.mvc.builder;

import java.util.List;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.web.portlet.context.ConfigurablePortletApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.portlet.PortletMvcViewFactory;
import org.springframework.webflow.mvc.servlet.ServletMvcViewFactory;
import org.springframework.webflow.mvc.view.FlowViewResolver;

/**
 * Returns {@link ViewFactory view factories} that create native Spring MVC-based views. Used by a FlowBuilder to
 * configure a flow's view states with Spring MVC-based view factories.
 * <p>
 * This implementation detects whether it is running in a Servlet or Portlet MVC environment, and returns instances of
 * the default view factory implementation for that environment.
 * <p>
 * By default, this implementation creates view factories that resolve their views by loading flow-relative resources,
 * such as .jsp templates located in a flow working directory. This class also supports rendering views resolved by
 * pre-existing Spring MVC {@link ViewResolver view resolvers}.
 * 
 * @see ServletMvcViewFactory
 * @see PortletMvcViewFactory
 * @see FlowResourceFlowViewResolver
 * @see DelegatingFlowViewResolver
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class MvcViewFactoryCreator implements ViewFactoryCreator, ApplicationContextAware {

	private MvcEnvironment environment;

	private FlowViewResolver flowViewResolver = new FlowResourceFlowViewResolver();

	/**
	 * Create a new Spring MVC View Factory Creator.
	 * @see #setDefaultViewSuffix(String)
	 * @see #setViewResolvers(List)
	 * @see #setFlowViewResolver(FlowViewResolver)
	 */
	public MvcViewFactoryCreator() {

	}

	/**
	 * Returns the configured mvc environment.
	 * @return the mvc environment
	 */
	public MvcEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * Sets the configured mvc environment.
	 * @param environment the mvc environment.
	 */
	public void setEnvironment(MvcEnvironment environment) {
		this.environment = environment;
	}

	/**
	 * Configure an {@link FlowResourceFlowViewResolver} capable of resolving view resources by applying the specified
	 * default resource suffix. Default is .jsp.
	 * @param defaultViewSuffix the default view suffix
	 */
	public void setDefaultViewSuffix(String defaultViewSuffix) {
		FlowResourceFlowViewResolver internalResourceResolver = new FlowResourceFlowViewResolver();
		internalResourceResolver.setDefaultViewSuffix(defaultViewSuffix);
		this.flowViewResolver = internalResourceResolver;
	}

	/**
	 * Sets the chain of Spring MVC {@link ViewResolver view resolvers} to delegate to resolve views selected by flows.
	 * Allows for reuse of existing View Resolvers configured in a Spring application context. If multiple resolvers are
	 * to be used, the resolvers should be ordered in the manner they should be applied.
	 * @param viewResolvers the view resolver list
	 */
	public void setViewResolvers(List viewResolvers) {
		this.flowViewResolver = new DelegatingFlowViewResolver(viewResolvers);
	}

	/**
	 * Set to fully customize how the flow system resolves Spring MVC {@link View} objects.
	 * @param flowViewResolver the flow view resolver
	 */
	public void setFlowViewResolver(FlowViewResolver flowViewResolver) {
		this.flowViewResolver = flowViewResolver;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		if (ClassUtils.isPresent("javax.portlet.PortletContext")
				&& applicationContext instanceof ConfigurablePortletApplicationContext) {
			environment = MvcEnvironment.PORTLET;
		} else {
			environment = MvcEnvironment.SERVLET;
		}
	}

	public ViewFactory createViewFactory(Expression viewId, ExpressionParser expressionParser,
			FormatterRegistry formatterRegistry) {
		if (environment == MvcEnvironment.SERVLET) {
			return new ServletMvcViewFactory(viewId, flowViewResolver, expressionParser, formatterRegistry);
		} else if (environment == MvcEnvironment.PORTLET) {
			return new PortletMvcViewFactory(viewId, flowViewResolver, expressionParser, formatterRegistry);
		} else {
			throw new IllegalStateException("Environment not supported " + environment);
		}
	}

	public String getViewIdByConvention(String viewStateId) {
		return flowViewResolver.getViewIdByConvention(viewStateId);
	}

}
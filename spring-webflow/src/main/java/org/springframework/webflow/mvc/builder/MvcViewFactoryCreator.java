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
import org.springframework.web.servlet.View;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.portlet.PortletMvcViewFactory;
import org.springframework.webflow.mvc.servlet.ServletMvcViewFactory;
import org.springframework.webflow.mvc.view.FlowViewResolver;

/**
 * View factory creator implementation that produces View Factories that create native Spring MVC-based views.
 * 
 * This class is used by a flow builder in a Spring MVC environment to configure view factories on flows that render
 * Spring MVC-based views.
 * 
 * This class supports rendering views resolved by existing Spring MVC-based resolver infrastructure, or, if no such
 * infrastructure is configured, JSP resources relative to the flow definition being built.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
public class MvcViewFactoryCreator implements ViewFactoryCreator {

	private MvcEnvironment environment;

	private String defaultViewSuffix = ".jsp";

	private FlowViewResolver flowViewResolver = new InternalResourceFlowViewResolver();

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
	 * Returns the default view suffix when selecting views by convention. Default is .jsp.
	 * @return the default view suffix
	 */
	public String getDefaultViewSuffix() {
		return defaultViewSuffix;
	}

	/**
	 * Sets the default suffix for view names when selecting views by convention. Default is .jsp.
	 * @param defaultViewSuffix the default view suffix
	 */
	public void setDefaultViewSuffix(String defaultViewSuffix) {
		this.defaultViewSuffix = defaultViewSuffix;
	}

	/**
	 * Sets the view resolvers that will be used to resolve views selected by flows. If multiple resolvers are to be
	 * used, the resolvers should be ordered in the manner they should be applied.
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

	public ViewFactory createViewFactory(Expression viewId, ExpressionParser expressionParser,
			FormatterRegistry formatterRegistry) {
		if (environment == null || environment == MvcEnvironment.SERVLET) {
			return new ServletMvcViewFactory(viewId, flowViewResolver, expressionParser, formatterRegistry);
		} else if (environment == MvcEnvironment.PORTLET) {
			return new PortletMvcViewFactory(viewId, flowViewResolver, expressionParser, formatterRegistry);
		} else {
			throw new IllegalStateException("Environment not supported " + environment);
		}
	}

	public String getViewIdByConvention(String viewStateId) {
		if (flowViewResolver instanceof DelegatingFlowViewResolver) {
			return viewStateId;
		} else {
			return viewStateId + defaultViewSuffix;
		}
	}

}
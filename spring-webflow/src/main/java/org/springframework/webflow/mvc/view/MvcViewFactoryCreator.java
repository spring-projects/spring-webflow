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
package org.springframework.webflow.mvc.view;

import java.util.List;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.ViewFactory;

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
public class MvcViewFactoryCreator implements ViewFactoryCreator, ApplicationContextAware {

	private List viewResolvers;

	private ApplicationContext applicationContext;

	/**
	 * Sets the view resolvers that will be used to resolve views selected by flows. If multiple resolvers are to be
	 * used, the resolvers should be ordered in the manner they should be applied.
	 * @param viewResolvers the view resolver list
	 */
	public void setViewResolvers(List viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	public void setApplicationContext(ApplicationContext context) {
		this.applicationContext = context;
	}

	public ViewFactory createViewFactory(Expression viewIdExpression, ExpressionParser expressionParser,
			FormatterRegistry formatterRegistry, ResourceLoader resourceLoader) {
		if (viewResolvers != null) {
			return new ViewResolvingMvcViewFactory(viewIdExpression, expressionParser, formatterRegistry, viewResolvers);
		} else {
			return new InternalFlowResourceMvcViewFactory(viewIdExpression, expressionParser, formatterRegistry,
					applicationContext, resourceLoader);
		}
	}

	public String getViewIdByConvention(String viewStateId) {
		// TODO - make configurable
		return viewStateId + ".jsp";
	}

}
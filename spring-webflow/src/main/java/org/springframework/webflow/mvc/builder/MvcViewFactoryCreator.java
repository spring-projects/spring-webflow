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

import org.springframework.beans.BeanWrapper;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.beanwrapper.BeanWrapperExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.ViewFactory;
import org.springframework.webflow.mvc.portlet.PortletMvcViewFactory;
import org.springframework.webflow.mvc.servlet.ServletMvcViewFactory;
import org.springframework.webflow.mvc.view.AbstractMvcViewFactory;
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

	private boolean useSpringBeanBinding;

	private String eventIdParameterName;

	private String fieldMarkerPrefix;

	/**
	 * Create a new Spring MVC View Factory Creator.
	 * @see #setDefaultViewSuffix(String)
	 * @see #setEventIdParameterName(String)
	 * @see #setFieldMarkerPrefix(String)
	 * @see #setUseSpringBeanBinding(boolean)
	 * @see #setFlowViewResolver(FlowViewResolver)
	 * @see #setViewResolvers(List)
	 */
	public MvcViewFactoryCreator() {

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
	 * Sets the name of the request parameter to use to lookup user events signaled by views created in this factory. If
	 * not specified, the default is <code>_eventId</code>
	 * @param eventIdParameterName the event id parameter name
	 */
	public void setEventIdParameterName(String eventIdParameterName) {
		this.eventIdParameterName = eventIdParameterName;
	}

	/**
	 * Specify a prefix that can be used for parameters that mark potentially empty fields, having "prefix + field" as
	 * name. Such a marker parameter is checked by existence: You can send any value for it, for example "visible". This
	 * is particularly useful for HTML checkboxes and select options.
	 * <p>
	 * Default is "_", for "_FIELD" parameters (e.g. "_subscribeToNewsletter"). Set this to null if you want to turn off
	 * the empty field check completely.
	 * <p>
	 * HTML checkboxes only send a value when they're checked, so it is not possible to detect that a formerly checked
	 * box has just been unchecked, at least not with standard HTML means.
	 * <p>
	 * This auto-reset mechanism addresses this deficiency, provided that a marker parameter is sent for each checkbox
	 * field, like "_subscribeToNewsletter" for a "subscribeToNewsletter" field. As the marker parameter is sent in any
	 * case, the data binder can detect an empty field and automatically reset its value.
	 */
	public void setFieldMarkerPrefix(String fieldMarkerPrefix) {
		this.fieldMarkerPrefix = fieldMarkerPrefix;
	}

	/**
	 * Sets whether to use data binding with Spring's {@link BeanWrapper} should be enabled. Set to 'true' to enable.
	 * 'false', disabled, is the default. With this enabled, the same binding system used by Spring MVC 2.x is also used
	 * in a Web Flow environment.
	 * @param useSpringBeanBinding the Spring bean binding flag
	 */
	public void setUseSpringBeanBinding(boolean useSpringBeanBinding) {
		this.useSpringBeanBinding = useSpringBeanBinding;
	}

	/**
	 * Set to fully customize how the flow system resolves Spring MVC {@link View} objects.
	 * @param flowViewResolver the flow view resolver
	 */
	public void setFlowViewResolver(FlowViewResolver flowViewResolver) {
		this.flowViewResolver = flowViewResolver;
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

	// implementing ApplicationContextAware

	public void setApplicationContext(ApplicationContext applicationContext) {
		environment = MvcEnvironment.environmentFor(applicationContext);
	}

	public ViewFactory createViewFactory(Expression viewId, ExpressionParser expressionParser,
			ConversionService conversionService, BinderConfiguration binderConfiguration) {
		if (useSpringBeanBinding) {
			expressionParser = new BeanWrapperExpressionParser(conversionService);
		}
		AbstractMvcViewFactory viewFactory = createMvcViewFactory(viewId, expressionParser, conversionService,
				binderConfiguration);
		if (StringUtils.hasText(eventIdParameterName)) {
			viewFactory.setEventIdParameterName(eventIdParameterName);
		}
		if (StringUtils.hasText(fieldMarkerPrefix)) {
			viewFactory.setFieldMarkerPrefix(fieldMarkerPrefix);
		}
		return viewFactory;
	}

	private AbstractMvcViewFactory createMvcViewFactory(Expression viewId, ExpressionParser expressionParser,
			ConversionService conversionService, BinderConfiguration binderConfiguration) {
		if (environment == MvcEnvironment.SERVLET) {
			return new ServletMvcViewFactory(viewId, flowViewResolver, expressionParser, conversionService,
					binderConfiguration);
		} else if (environment == MvcEnvironment.PORTLET) {
			return new PortletMvcViewFactory(viewId, flowViewResolver, expressionParser, conversionService,
					binderConfiguration);
		} else {
			throw new IllegalStateException("Web MVC Environment " + environment + " not supported ");
		}
	}

	public String getViewIdByConvention(String viewStateId) {
		return flowViewResolver.getViewIdByConvention(viewStateId);
	}

}
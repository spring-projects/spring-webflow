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
package org.springframework.webflow.mvc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
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

	private static final boolean jstlPresent = ClassUtils.isPresent("javax.servlet.jsp.jstl.fmt.LocalizationContext");

	// TODO
	private ExpressionParser expressionParser;

	// TODO
	private FormatterRegistry formatterRegistry;

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

	public ViewFactory createViewFactory(Expression viewIdExpression, ResourceLoader viewResourceLoader) {
		if (viewResolvers != null) {
			return new ViewResolvingMvcViewFactory(viewIdExpression, viewResolvers);
		} else {
			return new InternalFlowResourceMvcViewFactory(viewIdExpression, applicationContext, viewResourceLoader);
		}
	}

	public String getViewIdByConvention(String viewStateId) {
		// TODO - make configurable
		return viewStateId + ".jsp";
	}

	/**
	 * View factory implementation that creates a Spring-MVC Internal Resource view to render a flow-relative view
	 * resource such as a JSP or Velocity template.
	 * @author Keith Donald
	 */
	static class InternalFlowResourceMvcViewFactory implements ViewFactory {
		private Expression viewIdExpression;

		private ApplicationContext applicationContext;

		private ResourceLoader resourceLoader;

		public InternalFlowResourceMvcViewFactory(Expression viewIdExpression, ApplicationContext context,
				ResourceLoader resourceLoader) {
			this.viewIdExpression = viewIdExpression;
			this.applicationContext = context;
			this.resourceLoader = resourceLoader;
		}

		public View getView(RequestContext context) {
			String viewId = (String) viewIdExpression.getValue(context);
			if (viewId.startsWith("/")) {
				return getViewInternal(viewId, context);
			} else {
				ContextResource viewResource = (ContextResource) resourceLoader.getResource(viewId);
				return getViewInternal(viewResource.getPathWithinContext(), context);
			}
		}

		private View getViewInternal(String viewPath, RequestContext context) {
			if (viewPath.endsWith(".jsp")) {
				if (jstlPresent) {
					JstlView view = new JstlView(viewPath);
					view.setApplicationContext(applicationContext);
					return new MvcView(view, context);
				} else {
					InternalResourceView view = new InternalResourceView(viewPath);
					view.setApplicationContext(applicationContext);
					return new MvcView(view, context);
				}
			} else {
				throw new IllegalArgumentException("Unsupported view type " + viewPath
						+ " only types supported are [.jsp]");
			}
		}
	}

	/**
	 * View factory implementation that delegates to the Spring-configured view resolver chain to resolve the Spring MVC
	 * view implementation to render.
	 * @author Keith Donald
	 */
	private static class ViewResolvingMvcViewFactory implements ViewFactory {
		private Expression viewIdExpression;

		private List viewResolvers;

		public ViewResolvingMvcViewFactory(Expression viewIdExpression, List viewResolvers) {
			this.viewIdExpression = viewIdExpression;
			this.viewResolvers = viewResolvers;
		}

		public View getView(RequestContext context) {
			String view = (String) viewIdExpression.getValue(context);
			return new MvcView(resolveView(view), context);
		}

		protected org.springframework.web.servlet.View resolveView(String viewName) {
			for (Iterator it = viewResolvers.iterator(); it.hasNext();) {
				ViewResolver viewResolver = (ViewResolver) it.next();
				try {
					org.springframework.web.servlet.View view = viewResolver.resolveViewName(viewName,
							LocaleContextHolder.getLocale());
					if (view != null) {
						return view;
					}
				} catch (Exception e) {
					throw new IllegalStateException("Exception resolving view with name '" + viewName + "'", e);
				}
			}
			return null;
		}
	}

	private static class MvcView implements View {

		private RequestContext context;

		private org.springframework.web.servlet.View view;

		private String eventId;

		public MvcView(org.springframework.web.servlet.View view, RequestContext context) {
			this.view = view;
			this.context = context;
		}

		public void render() {
			Map model = new HashMap();
			model.putAll(context.getConversationScope().union(context.getFlowScope()).union(context.getFlashScope())
					.union(context.getRequestScope()).asMap());
			exposeBindingModel(model);
			model.put("flowRequestContext", context);
			model.put("flowExecutionKey", context.getFlowExecutionContext().getKey().toString());
			model.put("flowExecutionUrl", context.getFlowExecutionUrl());
			model.put("currentUser", context.getExternalContext().getCurrentUser());
			try {
				view.render(model, (HttpServletRequest) context.getExternalContext().getNativeRequest(),
						(HttpServletResponse) context.getExternalContext().getNativeResponse());
			} catch (Exception e) {
				throw new IllegalStateException("Exception rendering view", e);
			}
		}

		public void postback() {
			// TODO implement me with real data binding behavior
		}

		public boolean eventSignaled() {
			determineEventId(context);
			return eventId != null;
		}

		public Event getEvent() {
			return new Event(this, eventId, context.getRequestParameters().asAttributeMap());
		}

		private void exposeBindingModel(Map model) {
			Expression boundObjectExpr = (Expression) context.getCurrentState().getAttributes().get("model");
			if (boundObjectExpr != null) {
				Object boundObject = boundObjectExpr.getValue(context);
				// TODO
				BindingModel bindingModel = new BindingModel(boundObject, null, null, context.getMessageContext());
				MappingResults bindResults = (MappingResults) context.getRequestScope().get(
						boundObjectExpr.getExpressionString() + "MappingResults");
				bindingModel.setMappingResults(bindResults);
				model.put(BindingResult.MODEL_KEY_PREFIX + boundObjectExpr.getExpressionString(), model);
			}
		}

		private void determineEventId(RequestContext context) {
			eventId = findParameter("_eventId", context.getRequestParameters());
		}

		/**
		 * Obtain a named parameter from the request parameters. This method will try to obtain a parameter value using
		 * the following algorithm:
		 * <ol>
		 * <li>Try to get the parameter value using just the given <i>logical</i> name. This handles parameters of the
		 * form <tt>logicalName = value</tt>. For normal parameters, e.g. submitted using a hidden HTML form field,
		 * this will return the requested value.</li>
		 * <li>Try to obtain the parameter value from the parameter name, where the parameter name in the request is of
		 * the form <tt>logicalName_value = xyz</tt> with "_" being the configured delimiter. This deals with
		 * parameter values submitted using an HTML form submit button.</li>
		 * <li>If the value obtained in the previous step has a ".x" or ".y" suffix, remove that. This handles cases
		 * where the value was submitted using an HTML form image button. In this case the parameter in the request
		 * would actually be of the form <tt>logicalName_value.x = 123</tt>. </li>
		 * </ol>
		 * @param logicalParameterName the <i>logical</i> name of the request parameter
		 * @param parameters the available parameter map
		 * @return the value of the parameter, or <code>null</code> if the parameter does not exist in given request
		 */
		private String findParameter(String logicalParameterName, ParameterMap parameters) {
			// first try to get it as a normal name=value parameter
			String value = parameters.get(logicalParameterName);
			if (value != null) {
				return value;
			}
			// if no value yet, try to get it as a name_value=xyz parameter
			String prefix = logicalParameterName + "_";
			Iterator paramNames = parameters.asMap().keySet().iterator();
			while (paramNames.hasNext()) {
				String paramName = (String) paramNames.next();
				if (paramName.startsWith(prefix)) {
					String strValue = paramName.substring(prefix.length());
					// support images buttons, which would submit parameters as
					// name_value.x=123
					if (strValue.endsWith(".x") || strValue.endsWith(".y")) {
						strValue = strValue.substring(0, strValue.length() - 2);
					}
					return strValue;
				}
			}
			// we couldn't find the parameter value
			return null;
		}

	}

}
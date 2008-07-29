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

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * Base class for mvc view factories.
 * 
 * @author Keith Donald
 */
public abstract class AbstractMvcViewFactory implements ViewFactory {

	private Expression viewId;

	private FlowViewResolver viewResolver;

	private ExpressionParser expressionParser;

	private ConversionService conversionService;

	private BinderConfiguration binderConfiguration;

	private String eventIdParameterName;

	private String fieldMarkerPrefix;

	/**
	 * Creates a new MVC view factory.
	 * @param viewId the id of the view as an expression
	 * @param viewResolver the resolver to resolve the View implementation
	 * @param expressionParser the expression parser
	 * @param conversionService the conversion service
	 * @param binderConfiguration the model binding configuration
	 */
	public AbstractMvcViewFactory(Expression viewId, FlowViewResolver viewResolver, ExpressionParser expressionParser,
			ConversionService conversionService, BinderConfiguration binderConfiguration) {
		this.viewId = viewId;
		this.viewResolver = viewResolver;
		this.expressionParser = expressionParser;
		this.conversionService = conversionService;
		this.binderConfiguration = binderConfiguration;
	}

	public void setEventIdParameterName(String eventIdParameterName) {
		this.eventIdParameterName = eventIdParameterName;
	}

	public void setFieldMarkerPrefix(String fieldMarkerPrefix) {
		this.fieldMarkerPrefix = fieldMarkerPrefix;
	}

	public View getView(RequestContext context) {
		String viewId = (String) this.viewId.getValue(context);
		org.springframework.web.servlet.View view = viewResolver.resolveView(viewId, context);
		AbstractMvcView mvcView = createMvcView(view, context);
		mvcView.setExpressionParser(expressionParser);
		mvcView.setConversionService(conversionService);
		mvcView.setBinderConfiguration(binderConfiguration);
		if (StringUtils.hasText(eventIdParameterName)) {
			mvcView.setEventIdParameterName(eventIdParameterName);
		}
		if (StringUtils.hasText(fieldMarkerPrefix)) {
			mvcView.setFieldMarkerPrefix(fieldMarkerPrefix);
		}
		return mvcView;
	}

	protected abstract AbstractMvcView createMvcView(org.springframework.web.servlet.View view, RequestContext context);

}
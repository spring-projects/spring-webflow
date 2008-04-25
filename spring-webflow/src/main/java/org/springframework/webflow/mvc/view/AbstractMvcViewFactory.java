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

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * Base class for mvc view factories.
 * 
 * @author Keith Donald
 */
public abstract class AbstractMvcViewFactory implements ViewFactory {

	private Expression viewName;

	private FlowViewResolver viewResolver;

	private ExpressionParser expressionParser;

	private FormatterRegistry formatterRegistry;

	public AbstractMvcViewFactory(Expression viewName, FlowViewResolver viewResolver,
			ExpressionParser expressionParser, FormatterRegistry formatterRegistry) {
		this.viewName = viewName;
		this.viewResolver = viewResolver;
		this.expressionParser = expressionParser;
		this.formatterRegistry = formatterRegistry;
	}

	protected ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	protected FormatterRegistry getFormatterRegistry() {
		return formatterRegistry;
	}

	public View getView(RequestContext context) {
		String viewName = (String) this.viewName.getValue(context);
		org.springframework.web.servlet.View view = viewResolver.resolveView(viewName, context);
		return createMvcView(view, context);
	}

	protected abstract AbstractMvcView createMvcView(org.springframework.web.servlet.View view, RequestContext context);

}
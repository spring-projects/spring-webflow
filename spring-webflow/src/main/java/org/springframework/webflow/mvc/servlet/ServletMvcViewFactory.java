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
package org.springframework.webflow.mvc.servlet;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.web.servlet.View;
import org.springframework.webflow.engine.model.BinderModel;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.AbstractMvcView;
import org.springframework.webflow.mvc.view.AbstractMvcViewFactory;
import org.springframework.webflow.mvc.view.FlowViewResolver;

/**
 * Creates Servlet MVC views.
 * 
 * @author Keith Donald
 */
public class ServletMvcViewFactory extends AbstractMvcViewFactory {

	public ServletMvcViewFactory(Expression viewId, FlowViewResolver viewResolver, ExpressionParser expressionParser,
			ConversionService conversionService, BinderModel binderModel) {
		super(viewId, viewResolver, expressionParser, conversionService, binderModel);
	}

	protected AbstractMvcView createMvcView(View view, RequestContext context) {
		ServletMvcView mvcView = new ServletMvcView(view, context);
		mvcView.setExpressionParser(getExpressionParser());
		mvcView.setConversionService(getConversionService());
		mvcView.setBinderModel(getBinderModel());
		return mvcView;
	}

}

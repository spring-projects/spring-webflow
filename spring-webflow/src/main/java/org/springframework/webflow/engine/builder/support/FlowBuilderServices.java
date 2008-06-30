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
package org.springframework.webflow.engine.builder.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;

/**
 * A simple holder for configuring the services used by flow builders. These services are exposed to a builder in a
 * {@link FlowBuilderContext}.
 * 
 * Note this class does not attempt to default any service implementations other than the {@link FlowArtifactFactory},
 * which is more like builder helper objects than a service. It is expected clients inject non-null references to
 * concrete service implementations appropriate for their environment.
 * 
 * @see FlowBuilderContextImpl
 * 
 * @author Keith Donald
 */
public class FlowBuilderServices implements ApplicationContextAware, InitializingBean {

	/**
	 * The factory encapsulating the creation of central Flow artifacts such as {@link Flow flows} and
	 * {@link State states}.
	 */
	private FlowArtifactFactory flowArtifactFactory = new FlowArtifactFactory();

	/**
	 * The view factory creator for creating views to render during flow execution. The default is <code>null</code>
	 * and this service must be configured externally.
	 */
	private ViewFactoryCreator viewFactoryCreator;

	/**
	 * The conversion service for converting from one object type to another.
	 */
	private ConversionService conversionService;

	/**
	 * The parser for parsing expression strings into expression objects. The default is Web Flow's default expression
	 * parser implementation.
	 */
	private ExpressionParser expressionParser;

	/**
	 * The Spring application context that provides access to the services of the application.
	 */
	private ApplicationContext applicationContext;

	public FlowArtifactFactory getFlowArtifactFactory() {
		return flowArtifactFactory;
	}

	public void setFlowArtifactFactory(FlowArtifactFactory flowArtifactFactory) {
		this.flowArtifactFactory = flowArtifactFactory;
	}

	public ViewFactoryCreator getViewFactoryCreator() {
		return viewFactoryCreator;
	}

	public void setViewFactoryCreator(ViewFactoryCreator viewFactoryCreator) {
		this.viewFactoryCreator = viewFactoryCreator;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public ExpressionParser getExpressionParser() {
		return expressionParser;
	}

	public void setExpressionParser(ExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	// implementing ApplicationContextAware

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	// implementing InitializingBean

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(flowArtifactFactory, "The FlowArtifactFactory is required");
		Assert.notNull(viewFactoryCreator, "The ViewFactoryCreator is required");
		Assert.notNull(conversionService, "The type ConversionService is required");
		Assert.notNull(expressionParser, "The expressionParser is required");
		Assert.notNull(applicationContext, "The ApplicationContext is required");
	}
}
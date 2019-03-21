/*
 * Copyright 2004-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.faces.webflow.JsfResourceRequestHandler;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.config.FlowExecutorBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.executor.FlowExecutor;


/**
 * A base class for {@link Configuration @Configuration} classes to configure
 * Spring Web Flow in JSF applications.
 * <p>
 * Provides protected method access to builders for one (or more) of the following:
 * <ul>
 * 	<li>{@link FlowExecutor}
 * 	<li>{@link FlowDefinitionRegistry}
 * 	<li>{@link FlowBuilderServices}
 * </ul>
 * <p>
 * Also registers a HandlerMapping bean to provide JSF 2 resource handling at
 * {@code "/javax.faces.resource/**"} or Rich Faces at {@code "/rfRes/**"}.

 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class AbstractFacesFlowConfiguration implements ApplicationContextAware {

	private static final boolean isRichFacesPresent =
			ClassUtils.isPresent("org.richfaces.application.CoreConfiguration",
					ResourcesBeanDefinitionParser.class.getClassLoader());


	private ApplicationContext applicationContext;


	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	/**
	 * Return a builder for creating a {@link FlowExecutor} instance.
	 * @param flowRegistry the {@link FlowDefinitionRegistry} to configure on the flow executor
	 * @return the created builder
	 */
	protected FlowExecutorBuilder getFlowExecutorBuilder(FlowDefinitionLocator flowRegistry) {
		return new FlowExecutorBuilder(flowRegistry);
	}

	/**
	 * Return a builder for creating a {@link FlowDefinitionRegistry} instance.
	 * @return the created builder
	 */
	protected FlowDefinitionRegistryBuilder getFlowDefinitionRegistryBuilder() {
		return new FlowDefinitionRegistryBuilder(this.applicationContext);
	}

	/**
	 * Return a builder for creating a {@link FlowDefinitionRegistry} instance.
	 * @param flowBuilderServices the {@link FlowBuilderServices} to configure on the flow registry with
	 * @return the created builder
	 */
	protected FlowDefinitionRegistryBuilder getFlowDefinitionRegistryBuilder(FlowBuilderServices flowBuilderServices) {
		return new FlowDefinitionRegistryBuilder(this.applicationContext, flowBuilderServices);
	}

	/**
	 * Return a builder for creating a {@link FlowBuilderServices} instance.
	 * @return the created builder
	 */
	protected FlowBuilderServicesBuilder getFlowBuilderServicesBuilder() {
		return new FlowBuilderServicesBuilder();
	}

	@Bean
	public SimpleUrlHandlerMapping jsrResourceHandlerMapping() {

		Map<String, Object> urlMap = new HashMap<>();
		urlMap.put("/javax.faces.resource/**", jsfResourceRequestHandler());
		if (isRichFacesPresent) {
			urlMap.put("/rfRes/**", jsfResourceRequestHandler());
		}

		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setUrlMap(urlMap);
		handlerMapping.setOrder(0);
		return handlerMapping;
	}

	@Bean
	public JsfResourceRequestHandler jsfResourceRequestHandler() {
		return new JsfResourceRequestHandler();
	}

	@Bean
	public HttpRequestHandlerAdapter httpRequestHandlerAdapter() {
		return new HttpRequestHandlerAdapter();
	}

}

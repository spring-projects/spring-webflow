/*
 * Copyright 2004-2016 the original author or authors.
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

import java.util.Map;

import org.w3c.dom.Element;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

/**
 * Parser for the resources tag.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 */
public class ResourcesBeanDefinitionParser implements BeanDefinitionParser {

	private static final String SERVLET_RESOURCE_HANDLER_BEAN_NAME = "jsfResourceRequestHandler";

	private static final boolean isRichFacesPresent =
			ClassUtils.isPresent("org.richfaces.application.CoreConfiguration",
					ResourcesBeanDefinitionParser.class.getClassLoader());


	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		registerHandlerAdapterIfNecessary(source, parserContext);
		registerResourceHandler(source, parserContext);
		registerHandlerMappings(element, source, parserContext);
		return null;
	}


	private void registerHandlerAdapterIfNecessary(Object source, ParserContext parserContext) {
		String beanName = "org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter";
		if (parserContext.getRegistry().containsBeanDefinition(beanName)) {
			return;
		}
		RootBeanDefinition beanDefinition = new RootBeanDefinition(HttpRequestHandlerAdapter.class);
		beanDefinition.setSource(source);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
	}

	private void registerResourceHandler(Object source, ParserContext parserContext) {
		String beanName = "org.springframework.faces.webflow.JsfResourceRequestHandler";
		RootBeanDefinition beanDefinition = new RootBeanDefinition(beanName);
		beanDefinition.setSource(source);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(SERVLET_RESOURCE_HANDLER_BEAN_NAME, beanDefinition);
	}

	private void registerHandlerMappings(Element element, Object source, ParserContext parserContext) {
		Map<String, String> urlMap = new ManagedMap<>();
		urlMap.put("/jakarta.faces.resource/**", SERVLET_RESOURCE_HANDLER_BEAN_NAME);

		if (isRichFacesPresent) {
			urlMap.put("/rfRes/**", SERVLET_RESOURCE_HANDLER_BEAN_NAME);
		}

		RootBeanDefinition beanDefinition = new RootBeanDefinition(SimpleUrlHandlerMapping.class);
		beanDefinition.setSource(source);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		beanDefinition.getPropertyValues().add("urlMap", urlMap);

		String order = element.getAttribute("order");
		beanDefinition.getPropertyValues().add("order", StringUtils.hasText(order) ? order : 0);
		parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
	}

}

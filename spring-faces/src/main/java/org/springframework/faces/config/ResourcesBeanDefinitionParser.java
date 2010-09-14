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
package org.springframework.faces.config;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.w3c.dom.Element;

/**
 * Parser for the resources tag.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class ResourcesBeanDefinitionParser implements BeanDefinitionParser {

	static final String RESOURCE_HANDLER_BEAN_NAME = "jsfResourceRequestHandler";
	static final String RESOURCE_HANDLER_CLASS_NAME = "org.springframework.faces.webflow.JsfResourceRequestHandler";

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		registerHandlerAdapterIfNecessary(parserContext, source);
		registerResourceHandler(parserContext, source);
		registerHandlerMappings(element, parserContext, source);
		return null;
	}

	private void registerHandlerMappings(Element element, ParserContext parserContext, Object source) {
		Map<String, String> urlMap = new ManagedMap<String, String>();
		urlMap.put("/javax.faces.resource/**", RESOURCE_HANDLER_BEAN_NAME);

		RootBeanDefinition beanDefinition = new RootBeanDefinition(SimpleUrlHandlerMapping.class);
		beanDefinition.setSource(source);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		beanDefinition.getPropertyValues().add("urlMap", urlMap);

		String order = element.getAttribute("order");
		beanDefinition.getPropertyValues().add("order", StringUtils.hasText(order) ? order : 0);
		parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
	}

	private void registerResourceHandler(ParserContext parserContext, Object source) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition(RESOURCE_HANDLER_CLASS_NAME);
		beanDefinition.setSource(source);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(RESOURCE_HANDLER_BEAN_NAME, beanDefinition);
	}

	private void registerHandlerAdapterIfNecessary(ParserContext parserContext, Object source) {
		if (parserContext.getRegistry().containsBeanDefinition(
				"org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter")) {
			return;
		}
		RootBeanDefinition beanDefinition = new RootBeanDefinition(HttpRequestHandlerAdapter.class);
		beanDefinition.setSource(source);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
	}

}
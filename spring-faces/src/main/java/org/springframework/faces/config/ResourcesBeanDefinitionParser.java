/*
 * Copyright 2004-2012 the original author or authors.
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
import org.springframework.faces.webflow.JsfRuntimeInformation;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.w3c.dom.Element;

/**
 * Parser for the resources tag.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 */
public class ResourcesBeanDefinitionParser implements BeanDefinitionParser {

	static final String SERVLET_RESOURCE_HANDLER_BEAN_NAME = "jsfResourceRequestHandler";

	static final String PORTLET_RESOURCE_HANDLER_BEAN_NAME = "jsfPortletResourceRequestHandler";

	private static final boolean RICH_FACES_PRESENT =
			ClassUtils.isPresent("org.richfaces.application.CoreConfiguration",
					ResourcesBeanDefinitionParser.class.getClassLoader());

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		new ServletRegistrar(element, parserContext).register();
		if (JsfRuntimeInformation.isSpringPortletPresent()) {
			new PortletRegistrar(element, parserContext).register();
		}
		return null;
	}

	private static abstract class Registrar {

		protected final Element element;

		protected final ParserContext parserContext;

		protected final Object source;

		public Registrar(Element element, ParserContext parserContext) {
			this.element = element;
			this.parserContext = parserContext;
			this.source = parserContext.extractSource(element);
		}

		public abstract void register();
	}

	private static class ServletRegistrar extends Registrar {

		public ServletRegistrar(Element element, ParserContext parserContext) {
			super(element, parserContext);
		}

		@Override
		public void register() {
			registerHandlerAdapterIfNecessary();
			registerResourceHandler();
			registerHandlerMappings();
		}

		private void registerHandlerAdapterIfNecessary() {
			if (parserContext.getRegistry().containsBeanDefinition("org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter")) {
				return;
			}
			RootBeanDefinition beanDefinition = new RootBeanDefinition(HttpRequestHandlerAdapter.class);
			beanDefinition.setSource(source);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			parserContext.getReaderContext().registerWithGeneratedName(beanDefinition);
		}

		private void registerResourceHandler() {
			RootBeanDefinition beanDefinition = new RootBeanDefinition("org.springframework.faces.webflow.JsfResourceRequestHandler");
			beanDefinition.setSource(source);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			parserContext.getRegistry().registerBeanDefinition(SERVLET_RESOURCE_HANDLER_BEAN_NAME, beanDefinition);
		}

		private void registerHandlerMappings() {
			Map<String, String> urlMap = new ManagedMap<String, String>();
			urlMap.put("/javax.faces.resource/**", SERVLET_RESOURCE_HANDLER_BEAN_NAME);

			if (RICH_FACES_PRESENT) {
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

	private static class PortletRegistrar extends Registrar {

		public PortletRegistrar(Element element, ParserContext parserContext) {
			super(element, parserContext);
		}

		@Override
		public void register() {
			RootBeanDefinition beanDefinition = new RootBeanDefinition("org.springframework.faces.webflow.context.portlet.JsfResourceRequestHandler");
			beanDefinition.setSource(source);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			parserContext.getRegistry().registerBeanDefinition(PORTLET_RESOURCE_HANDLER_BEAN_NAME, beanDefinition);
		}
	}

}
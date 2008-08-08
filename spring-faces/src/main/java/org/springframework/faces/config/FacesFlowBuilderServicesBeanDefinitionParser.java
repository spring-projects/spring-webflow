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

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.el.DefaultExpressionFactoryUtils;
import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.faces.webflow.JsfManagedBeanAwareELExpressionParser;
import org.springframework.faces.webflow.JsfViewFactoryCreator;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;
import org.w3c.dom.Element;

/**
 * Parser for the flow-builder-services tag.
 * 
 * @author Jeremy Grelle
 */
public class FacesFlowBuilderServicesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser implements
		BeanDefinitionParser {

	protected Class getBeanClass(Element element) {
		return FlowBuilderServices.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder definitionBuilder) {
		boolean enableManagedBeans = parseEnableManagedBeans(element, definitionBuilder);
		if (enableManagedBeans) {
			definitionBuilder.addPropertyValue("expressionParser", new JsfManagedBeanAwareELExpressionParser(
					DefaultExpressionFactoryUtils.createExpressionFactory()));
		} else {
			parseExpressionParser(element, definitionBuilder);
		}
		parseConversionService(element, definitionBuilder);
		parseViewFactoryCreator(element, definitionBuilder);
	}

	private boolean parseEnableManagedBeans(Element element, BeanDefinitionBuilder definitionBuilder) {
		String enableManagedBeans = element.getAttribute("enable-managed-beans");
		if (StringUtils.hasText(enableManagedBeans)) {
			return Boolean.valueOf(enableManagedBeans).booleanValue();
		} else {
			return false;
		}
	}

	private void parseConversionService(Element element, BeanDefinitionBuilder definitionBuilder) {
		String conversionService = element.getAttribute("conversion-service");
		if (StringUtils.hasText(conversionService)) {
			definitionBuilder.addPropertyReference("conversionService", conversionService);
		} else {
			definitionBuilder.addPropertyValue("conversionService", new FacesConversionService());
		}
	}

	private void parseViewFactoryCreator(Element element, BeanDefinitionBuilder definitionBuilder) {
		String viewFactoryCreator = element.getAttribute("view-factory-creator");
		if (StringUtils.hasText(viewFactoryCreator)) {
			definitionBuilder.addPropertyReference("viewFactoryCreator", viewFactoryCreator);
		} else {
			definitionBuilder.addPropertyValue("viewFactoryCreator", new JsfViewFactoryCreator());
		}
	}

	private void parseExpressionParser(Element element, BeanDefinitionBuilder definitionBuilder) {
		String expressionParser = element.getAttribute("expression-parser");
		if (StringUtils.hasText(expressionParser)) {
			definitionBuilder.addPropertyReference("expressionParser", expressionParser);
		} else {
			Object value = definitionBuilder.getBeanDefinition().getPropertyValues().getPropertyValue(
					"conversionService");
			if (value instanceof RuntimeBeanReference) {
				BeanDefinitionBuilder builder = BeanDefinitionBuilder
						.genericBeanDefinition(WebFlowELExpressionParser.class);
				builder.addConstructorArgValue(DefaultExpressionFactoryUtils.createExpressionFactory());
				builder.addPropertyValue("conversionService", value);
				definitionBuilder.addPropertyValue("expressionParser", builder.getBeanDefinition());
			} else {
				ConversionService conversionService = (ConversionService) value;
				WebFlowELExpressionParser elExpressionParser = new WebFlowELExpressionParser(
						DefaultExpressionFactoryUtils.createExpressionFactory());
				elExpressionParser.setConversionService(conversionService);
				definitionBuilder.addPropertyValue("expressionParser", elExpressionParser);
			}
		}
	}
}

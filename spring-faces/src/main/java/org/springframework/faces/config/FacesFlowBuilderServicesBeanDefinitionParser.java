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
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
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

	// --------------------------- XML Config Attributes ----------------------- //
	private static final String CONVERSION_SERVICE_ATTR = "conversion-service";
	private static final String DEVELOPMENT_ATTR = "development";
	private static final String ENABLE_MANAGED_BEANS_ATTR = "enable-managed-beans";
	private static final String EXPRESSION_PARSER_ATTR = "expression-parser";
	private static final String VIEW_FACTORY_CREATOR_ATTR = "view-factory-creator";

	// --------------------------- Bean Configuration Properties --------------------- //
	private static final String CONVERSION_SERVICE_PROPERTY = "conversionService";
	private static final String DEVELOPMENT_PROPERTY = "development";
	private static final String EXPRESSION_PARSER_PROPERTY = "expressionParser";
	private static final String VIEW_FACTORY_CREATOR_PROPERTY = "viewFactoryCreator";

	protected Class getBeanClass(Element element) {
		return FlowBuilderServices.class;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder definitionBuilder) {
		parseConversionService(element, parserContext, definitionBuilder);
		parseExpressionParser(element, parserContext, definitionBuilder, parseEnableManagedBeans(element,
				definitionBuilder));
		parseViewFactoryCreator(element, parserContext, definitionBuilder);
		parseDevelopment(element, definitionBuilder);
	}

	private boolean parseEnableManagedBeans(Element element, BeanDefinitionBuilder definitionBuilder) {
		String enableManagedBeans = element.getAttribute(ENABLE_MANAGED_BEANS_ATTR);
		if (StringUtils.hasText(enableManagedBeans)) {
			return Boolean.valueOf(enableManagedBeans).booleanValue();
		} else {
			return false;
		}
	}

	private void parseConversionService(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder) {
		String conversionService = element.getAttribute(CONVERSION_SERVICE_ATTR);
		if (!StringUtils.hasText(conversionService)) {
			BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(FacesConversionService.class);
			conversionService = BeanDefinitionReaderUtils.registerWithGeneratedName(conversionServiceBuilder
					.getBeanDefinition(), context.getRegistry());
		}
		definitionBuilder.addPropertyReference(CONVERSION_SERVICE_PROPERTY, conversionService);
	}

	private void parseViewFactoryCreator(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder) {
		String viewFactoryCreator = element.getAttribute(VIEW_FACTORY_CREATOR_ATTR);
		if (!StringUtils.hasText(viewFactoryCreator)) {
			BeanDefinitionBuilder viewFactoryCreatorBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(JsfViewFactoryCreator.class);
			viewFactoryCreator = BeanDefinitionReaderUtils.registerWithGeneratedName(viewFactoryCreatorBuilder
					.getBeanDefinition(), context.getRegistry());
		}
		definitionBuilder.addPropertyReference(VIEW_FACTORY_CREATOR_PROPERTY, viewFactoryCreator);
	}

	private void parseExpressionParser(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder,
			boolean enableManagedBeans) {

		String conversionService = getConversionService(definitionBuilder);
		String expressionParser = element.getAttribute(EXPRESSION_PARSER_ATTR);

		BeanDefinitionBuilder expressionFactoryBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(DefaultExpressionFactoryUtils.class);
		expressionFactoryBuilder.setFactoryMethod("createExpressionFactory");

		if (!StringUtils.hasText(expressionParser)) {

			BeanDefinitionBuilder expressionParserBuilder;

			if (enableManagedBeans) {
				expressionParserBuilder = BeanDefinitionBuilder
						.genericBeanDefinition(JsfManagedBeanAwareELExpressionParser.class);
			} else {
				expressionParserBuilder = BeanDefinitionBuilder.genericBeanDefinition(WebFlowELExpressionParser.class);
			}

			expressionParserBuilder.addConstructorArgValue(expressionFactoryBuilder.getBeanDefinition());
			expressionParserBuilder.addPropertyReference(CONVERSION_SERVICE_PROPERTY, conversionService);
			expressionParser = BeanDefinitionReaderUtils.registerWithGeneratedName(expressionParserBuilder
					.getBeanDefinition(), context.getRegistry());
		} else if (enableManagedBeans) {
			throw new IllegalStateException(
					"Do not specify a custom expression-parser when enable-managed-beans is true");
		}
		definitionBuilder.addPropertyReference(EXPRESSION_PARSER_PROPERTY, expressionParser);

	}

	private String getConversionService(BeanDefinitionBuilder definitionBuilder) {
		RuntimeBeanReference conversionServiceReference = (RuntimeBeanReference) definitionBuilder.getBeanDefinition()
				.getPropertyValues().getPropertyValue(CONVERSION_SERVICE_PROPERTY).getValue();
		return conversionServiceReference.getBeanName();
	}

	private void parseDevelopment(Element element, BeanDefinitionBuilder definitionBuilder) {
		String development = element.getAttribute(DEVELOPMENT_ATTR);
		if (StringUtils.hasText(development)) {
			definitionBuilder.addPropertyValue(DEVELOPMENT_PROPERTY, development);
		}
	}
}
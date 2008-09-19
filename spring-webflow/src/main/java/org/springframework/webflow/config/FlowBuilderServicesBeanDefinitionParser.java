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
package org.springframework.webflow.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;flow-builder-services&gt;</code> tag.
 * 
 * @author Jeremy Grelle
 */
class FlowBuilderServicesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	// --------------------------- Full qualified class names ----------------------- //
	private static final String DEFAULT_EXPRESSION_PARSER_FACTORY_CLASS_NAME = "org.springframework.webflow.expression.DefaultExpressionParserFactory";
	private static final String DEFAULT_CONVERSION_SERVICE_CLASS_NAME = "org.springframework.binding.convert.service.DefaultConversionService";
	private static final String FLOW_BUILDER_SERVICES_CLASS_NAME = "org.springframework.webflow.engine.builder.support.FlowBuilderServices";
	private static final String MVC_VIEW_FACTORY_CREATOR_CLASS_NAME = "org.springframework.webflow.mvc.builder.MvcViewFactoryCreator";

	// --------------------------- XML Config Attributes ----------------------- //
	private static final String CONVERSION_SERVICE_ATTR = "conversion-service";
	private static final String DEVELOPMENT_ATTR = "development";
	private static final String EXPRESSION_PARSER_ATTR = "expression-parser";
	private static final String VIEW_FACTORY_CREATOR_ATTR = "view-factory-creator";

	// --------------------------- Bean Configuration Properties --------------------- //
	private static final String CONVERSION_SERVICE_PROPERTY = "conversionService";
	private static final String DEVELOPMENT_PROPERTY = "development";
	private static final String EXPRESSION_PARSER_PROPERTY = "expressionParser";
	private static final String VIEW_FACTORY_CREATOR_PROPERTY = "viewFactoryCreator";

	protected String getBeanClassName(Element element) {
		return FLOW_BUILDER_SERVICES_CLASS_NAME;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		CompositeComponentDefinition componentDefinition = new CompositeComponentDefinition(element.getLocalName(),
				parserContext.extractSource(element));
		parserContext.pushContainingComponent(componentDefinition);

		parseConversionService(element, parserContext, builder);
		parseExpressionParser(element, parserContext, builder);
		parseViewFactoryCreator(element, parserContext, builder);
		parseDevelopment(element, builder);

		parserContext.popAndRegisterContainingComponent();
	}

	private void parseConversionService(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder) {
		String conversionService = element.getAttribute(CONVERSION_SERVICE_ATTR);
		if (!StringUtils.hasText(conversionService)) {
			BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(DEFAULT_CONVERSION_SERVICE_CLASS_NAME);
			conversionService = registerInfrastructureComponent(element, context, conversionServiceBuilder);
		}
		definitionBuilder.addPropertyReference(CONVERSION_SERVICE_PROPERTY, conversionService);
	}

	private void parseExpressionParser(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder) {
		String expressionParser = element.getAttribute(EXPRESSION_PARSER_ATTR);
		if (!StringUtils.hasText(expressionParser)) {
			String conversionService = getConversionService(definitionBuilder);
			BeanDefinitionBuilder expressionParserBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(DEFAULT_EXPRESSION_PARSER_FACTORY_CLASS_NAME);
			expressionParserBuilder.setFactoryMethod("getExpressionParser");
			expressionParserBuilder.addConstructorArgReference(conversionService);
			expressionParser = registerInfrastructureComponent(element, context, expressionParserBuilder);
		}
		definitionBuilder.addPropertyReference(EXPRESSION_PARSER_PROPERTY, expressionParser);
	}

	private void parseViewFactoryCreator(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder) {
		String viewFactoryCreator = element.getAttribute(VIEW_FACTORY_CREATOR_ATTR);
		if (!StringUtils.hasText(viewFactoryCreator)) {
			BeanDefinitionBuilder viewFactoryCreatorBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(MVC_VIEW_FACTORY_CREATOR_CLASS_NAME);
			viewFactoryCreator = registerInfrastructureComponent(element, context, viewFactoryCreatorBuilder);
		}
		definitionBuilder.addPropertyReference(VIEW_FACTORY_CREATOR_PROPERTY, viewFactoryCreator);
	}

	private void parseDevelopment(Element element, BeanDefinitionBuilder definitionBuilder) {
		String development = element.getAttribute(DEVELOPMENT_ATTR);
		if (StringUtils.hasText(development)) {
			definitionBuilder.addPropertyValue(DEVELOPMENT_PROPERTY, development);
		}
	}

	private String getConversionService(BeanDefinitionBuilder definitionBuilder) {
		RuntimeBeanReference conversionServiceReference = (RuntimeBeanReference) definitionBuilder.getBeanDefinition()
				.getPropertyValues().getPropertyValue(CONVERSION_SERVICE_PROPERTY).getValue();
		return conversionServiceReference.getBeanName();
	}

	private String registerInfrastructureComponent(Element element, ParserContext context,
			BeanDefinitionBuilder componentBuilder) {
		String beanName = context.getReaderContext().generateBeanName(componentBuilder.getRawBeanDefinition());
		componentBuilder.getRawBeanDefinition().setSource(context.extractSource(element));
		componentBuilder.getRawBeanDefinition().setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		context.registerBeanComponent(new BeanComponentDefinition(componentBuilder.getBeanDefinition(),
				beanName));
		return beanName;
	}
}
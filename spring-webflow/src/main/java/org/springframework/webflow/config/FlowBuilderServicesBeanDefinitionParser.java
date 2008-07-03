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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;flow-builder-services&gt;</code> tag.
 * 
 * @author Jeremy Grelle
 */
class FlowBuilderServicesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	protected Class getBeanClass(Element element) {
		return FlowBuilderServices.class;
	}

	protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder builder) {
		parseConversionService(element, builder, context);
		parseExpressionParser(element, builder, context);
		parseViewFactoryCreator(element, builder, context);
	}

	private void parseConversionService(Element element, BeanDefinitionBuilder definitionBuilder, ParserContext context) {
		String conversionService = element.getAttribute("conversion-service");
		if (StringUtils.hasText(conversionService)) {
			definitionBuilder.addPropertyReference("conversionService", conversionService);
		} else {
			definitionBuilder.addPropertyValue("conversionService", new DefaultConversionService());
		}
	}

	private void parseExpressionParser(Element element, BeanDefinitionBuilder definitionBuilder, ParserContext context) {
		String expressionParser = element.getAttribute("expression-parser");
		if (StringUtils.hasText(expressionParser)) {
			definitionBuilder.addPropertyReference("expressionParser", expressionParser);
		} else {
			definitionBuilder
					.addPropertyValue("expressionParser", DefaultExpressionParserFactory.getExpressionParser());
		}
	}

	private void parseViewFactoryCreator(Element element, BeanDefinitionBuilder definitionBuilder, ParserContext context) {
		String viewFactoryCreator = element.getAttribute("view-factory-creator");
		if (StringUtils.hasText(viewFactoryCreator)) {
			definitionBuilder.addPropertyReference("viewFactoryCreator", viewFactoryCreator);
		} else {
			definitionBuilder.addPropertyValue("viewFactoryCreator", BeanDefinitionBuilder.genericBeanDefinition(
					MvcViewFactoryCreator.class).getBeanDefinition());
		}
	}

}
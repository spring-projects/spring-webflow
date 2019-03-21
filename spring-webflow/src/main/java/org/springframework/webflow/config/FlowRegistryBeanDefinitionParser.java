/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.webflow.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the flow <code>&lt;flow-registry&gt;</code> tag.
 * 
 * @author Keith Donald
 * @author Scott Andrews
 */
class FlowRegistryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	// --------------------------- Full qualified class names ----------------------- //
	private static final String DEFAULT_CONVERSION_SERVICE_CLASS_NAME = "org.springframework.binding.convert.service.DefaultConversionService";
	private static final String WEB_FLOW_SPRING_EL_EXPRESSION_PARSER_CLASS_NAME = "org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser";
	private static final String SPRING_EL_EXPRESSION_PARSER_CLASS_NAME = "org.springframework.expression.spel.standard.SpelExpressionParser";
	private static final String FLOW_BUILDER_SERVICES_CLASS_NAME = "org.springframework.webflow.engine.builder.support.FlowBuilderServices";
	private static final String FLOW_REGISTRY_FACTORY_BEAN_CLASS_NAME = "org.springframework.webflow.config.FlowRegistryFactoryBean";
	private static final String MVC_VIEW_FACTORY_CREATOR_CLASS_NAME = "org.springframework.webflow.mvc.builder.MvcViewFactoryCreator";

	protected String getBeanClassName(Element element) {
		return FLOW_REGISTRY_FACTORY_BEAN_CLASS_NAME;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder definitionBuilder) {
		CompositeComponentDefinition componentDefinition = new CompositeComponentDefinition(element.getLocalName(),
				parserContext.extractSource(element));
		parserContext.pushContainingComponent(componentDefinition);

		parseFlowBuilderServices(element, parserContext, definitionBuilder);

		String parent = element.getAttribute("parent");
		if (StringUtils.hasText(parent)) {
			definitionBuilder.addPropertyReference("parent", parent);
		}

		String basePath = element.getAttribute("base-path");
		if (StringUtils.hasText(basePath)) {
			definitionBuilder.addPropertyValue("basePath", basePath);
		}

		definitionBuilder.addPropertyValue("flowLocations", parseLocations(element));
		definitionBuilder.addPropertyValue("flowLocationPatterns", parseLocationPatterns(element));
		definitionBuilder.addPropertyValue("flowBuilders", parseFlowBuilders(element));

		parserContext.popAndRegisterContainingComponent();
	}

	private List<FlowLocation> parseLocations(Element element) {
		List<Element> locationElements = DomUtils.getChildElementsByTagName(element, "flow-location");
		if (locationElements.isEmpty()) {
			return Collections.emptyList();
		}
		List<FlowLocation> locations = new ArrayList<>(locationElements.size());
		for (Element locationElement : locationElements) {
			String id = locationElement.getAttribute("id");
			String path = locationElement.getAttribute("path");
			locations.add(new FlowLocation(id, path, parseAttributes(locationElement)));
		}
		return locations;
	}

	private List<String> parseLocationPatterns(Element element) {
		List<Element> locationPatternElements = DomUtils.getChildElementsByTagName(element, "flow-location-pattern");
		if (locationPatternElements.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> locationPatterns = new ArrayList<>(locationPatternElements.size());
		for (Element locationPatternElement : locationPatternElements) {
			String value = locationPatternElement.getAttribute("value");
			locationPatterns.add(value);
		}
		return locationPatterns;
	}

	private Set<FlowElementAttribute> parseAttributes(Element element) {
		Element definitionAttributesElement = DomUtils.getChildElementByTagName(element, "flow-definition-attributes");
		if (definitionAttributesElement != null) {
			List<Element> attributeElements = DomUtils.getChildElementsByTagName(definitionAttributesElement,
					"attribute");
			Set<FlowElementAttribute> attributes = new HashSet<>(attributeElements.size());
			for (Element attributeElement : attributeElements) {
				String name = attributeElement.getAttribute("name");
				String value = attributeElement.getAttribute("value");
				String type = attributeElement.getAttribute("type");
				attributes.add(new FlowElementAttribute(name, value, type));
			}
			return attributes;
		} else {
			return null;
		}
	}

	private List<FlowBuilderInfo> parseFlowBuilders(Element element) {
		List<Element> builderElements = DomUtils.getChildElementsByTagName(element, "flow-builder");
		if (builderElements.isEmpty()) {
			return Collections.emptyList();
		}
		List<FlowBuilderInfo> builders = new ArrayList<>(builderElements.size());
		for (Element builderElement : builderElements) {
			String id = builderElement.getAttribute("id");
			String className = builderElement.getAttribute("class");
			builders.add(new FlowBuilderInfo(id, className, parseAttributes(builderElement)));
		}
		return builders;
	}

	private void parseFlowBuilderServices(Element element, ParserContext context,
			BeanDefinitionBuilder definitionBuilder) {
		String flowBuilderServices = element.getAttribute("flow-builder-services");
		if (!StringUtils.hasText(flowBuilderServices)) {

			BeanDefinitionBuilder flowBuilderServicesBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(FLOW_BUILDER_SERVICES_CLASS_NAME);

			BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(DEFAULT_CONVERSION_SERVICE_CLASS_NAME);
			String conversionService = registerInfrastructureComponent(element, context, conversionServiceBuilder);
			flowBuilderServicesBuilder.addPropertyReference("conversionService", conversionService);

			BeanDefinitionBuilder springElExpressionParserBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(SPRING_EL_EXPRESSION_PARSER_CLASS_NAME);
			BeanDefinitionBuilder webFlowElExpressionParserBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(WEB_FLOW_SPRING_EL_EXPRESSION_PARSER_CLASS_NAME);
			webFlowElExpressionParserBuilder
					.addConstructorArgValue(springElExpressionParserBuilder.getBeanDefinition());

			String expressionParser = registerInfrastructureComponent(element, context,
					webFlowElExpressionParserBuilder);
			flowBuilderServicesBuilder.addPropertyReference("expressionParser", expressionParser);

			BeanDefinitionBuilder viewFactoryCreatorBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(MVC_VIEW_FACTORY_CREATOR_CLASS_NAME);
			String viewFactoryCreator = registerInfrastructureComponent(element, context, viewFactoryCreatorBuilder);
			flowBuilderServicesBuilder.addPropertyReference("viewFactoryCreator", viewFactoryCreator);

			flowBuilderServices = registerInfrastructureComponent(element, context, flowBuilderServicesBuilder);
		}
		definitionBuilder.addPropertyReference("flowBuilderServices", flowBuilderServices);
	}

	private String registerInfrastructureComponent(Element element, ParserContext context,
			BeanDefinitionBuilder viewFactoryCreatorBuilder) {
		String beanName = context.getReaderContext().generateBeanName(viewFactoryCreatorBuilder.getRawBeanDefinition());
		viewFactoryCreatorBuilder.getRawBeanDefinition().setSource(context.extractSource(element));
		viewFactoryCreatorBuilder.getRawBeanDefinition().setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		context.registerBeanComponent(new BeanComponentDefinition(viewFactoryCreatorBuilder.getBeanDefinition(),
				beanName));
		return beanName;
	}

}

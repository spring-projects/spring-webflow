package org.springframework.faces.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.faces.expression.LegacyJSFELExpressionParser;
import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.faces.webflow.JsfViewFactoryCreator;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;
import org.w3c.dom.Element;

public class FacesFlowBuilderServicesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser implements
		BeanDefinitionParser {

	private static final String ENABLE_MANAGED_BEANS_ATTRIBUTE = "enable-managed-beans";

	private static final String EXPRESSION_PARSER_ATTRIBUTE = "expression-parser";

	private static final String EXPRESSION_PARSER_PROPERTY = "expressionParser";

	private static final String VIEW_FACTORY_CREATOR_ATTRIBUTE = "view-factory-creator";

	private static final String VIEW_FACTORY_CREATOR_PROPERTY = "viewFactoryCreator";

	private static final String CONVERSION_SERVICE_ATTRIBUTE = "conversion-service";

	private static final String CONVERSION_SERVICE_PROPERTY = "conversionService";

	protected Class getBeanClass(Element element) {
		return FlowBuilderServices.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder definitionBuilder) {
		boolean enableManagedBeans = parseEnableManagedBeans(element, definitionBuilder);
		if (!enableManagedBeans) {
			parseExpressionParser(element, definitionBuilder);
		}
		parseViewFactoryCreator(element, definitionBuilder);
		parseConversionService(element, definitionBuilder);
	}

	private boolean parseEnableManagedBeans(Element element, BeanDefinitionBuilder definitionBuilder) {
		String enableManagedBeans = element.getAttribute(ENABLE_MANAGED_BEANS_ATTRIBUTE);
		if (StringUtils.hasText(enableManagedBeans)) {
			boolean enabled = Boolean.parseBoolean(enableManagedBeans);
			if (enabled) {
				definitionBuilder.addPropertyValue(EXPRESSION_PARSER_PROPERTY, new LegacyJSFELExpressionParser());
			}
			return enabled;
		}
		return false;
	}

	private void parseConversionService(Element element, BeanDefinitionBuilder definitionBuilder) {
		String conversionService = element.getAttribute(CONVERSION_SERVICE_ATTRIBUTE);
		if (StringUtils.hasText(conversionService)) {
			definitionBuilder.addPropertyReference(CONVERSION_SERVICE_PROPERTY, conversionService);
		} else {
			definitionBuilder.addPropertyValue(CONVERSION_SERVICE_PROPERTY, new FacesConversionService());
		}
	}

	private void parseViewFactoryCreator(Element element, BeanDefinitionBuilder definitionBuilder) {
		String viewFactoryCreator = element.getAttribute(VIEW_FACTORY_CREATOR_ATTRIBUTE);
		if (StringUtils.hasText(viewFactoryCreator)) {
			definitionBuilder.addPropertyReference(VIEW_FACTORY_CREATOR_PROPERTY, viewFactoryCreator);
		} else {
			definitionBuilder.addPropertyValue(VIEW_FACTORY_CREATOR_PROPERTY, new JsfViewFactoryCreator());
		}
	}

	private void parseExpressionParser(Element element, BeanDefinitionBuilder definitionBuilder) {
		String expressionParser = element.getAttribute(EXPRESSION_PARSER_ATTRIBUTE);
		if (StringUtils.hasText(expressionParser)) {
			definitionBuilder.addPropertyReference(EXPRESSION_PARSER_PROPERTY, expressionParser);
		} else {
			definitionBuilder.addPropertyValue(EXPRESSION_PARSER_PROPERTY, new WebFlowELExpressionParser());
		}
	}
}

package org.springframework.webflow.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.mvc.MvcViewFactoryCreator;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;flow-builder-services&gt;</code> tag.
 * 
 * @author Jeremy Grelle
 */
public class FlowBuilderServicesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

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
		parseExpressionParser(element, definitionBuilder);
		parseViewFactoryCreator(element, definitionBuilder);
		parseConversionService(element, definitionBuilder);
	}

	private void parseConversionService(Element element, BeanDefinitionBuilder definitionBuilder) {
		String conversionService = element.getAttribute(CONVERSION_SERVICE_ATTRIBUTE);
		if (StringUtils.hasText(conversionService)) {
			definitionBuilder.addPropertyReference(CONVERSION_SERVICE_PROPERTY, conversionService);
		}
	}

	private void parseViewFactoryCreator(Element element, BeanDefinitionBuilder definitionBuilder) {
		String viewFactoryCreator = element.getAttribute(VIEW_FACTORY_CREATOR_ATTRIBUTE);
		if (StringUtils.hasText(viewFactoryCreator)) {
			definitionBuilder.addPropertyReference(VIEW_FACTORY_CREATOR_PROPERTY, viewFactoryCreator);
		} else {
			definitionBuilder.addPropertyValue(VIEW_FACTORY_CREATOR_PROPERTY, new MvcViewFactoryCreator());
		}
	}

	private void parseExpressionParser(Element element, BeanDefinitionBuilder definitionBuilder) {
		String expressionParser = element.getAttribute(EXPRESSION_PARSER_ATTRIBUTE);
		if (StringUtils.hasText(expressionParser)) {
			definitionBuilder.addPropertyReference(EXPRESSION_PARSER_PROPERTY, expressionParser);
		}
	}

}

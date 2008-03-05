/*
 * Copyright 2004-2007 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the flow <code>&lt;registry&gt;</code> tag.
 * 
 * @author Keith Donald
 */
class FlowRegistryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	private static final String FLOW_BUILDER_SERVICES_ATTRIBUTE = "flow-builder-services";

	private static final String FLOW_LOCATION_ELEMENT = "flow-location";

	private static final String FLOW_BUILDER_ELEMENT = "flow-builder";

	private static final String ID_ATTRIBUTE = "id";

	private static final String PATH_ATTRIBUTE = "path";

	private static final String CLASS_ATTRIBUTE = "class";

	private static final String DEFINITION_ATTRIBUTES_ELEMENT = "flow-definition-attributes";

	private static final String ATTRIBUTE_ELEMENT = "attribute";

	private static final String NAME_ATTRIBUTE = "name";

	private static final String VALUE_ATTRIBUTE = "value";

	private static final String TYPE_ATTRIBUTE = "type";

	private static final String FLOW_LOCATIONS_PROPERTY = "flowLocations";

	private static final String FLOW_BUILDERS_PROPERTY = "flowBuilders";

	private static final String FLOW_BUILDER_SERVICES_PROPERTY = "flowBuilderServices";

	protected Class getBeanClass(Element element) {
		return FlowRegistryFactoryBean.class;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder definitionBuilder) {
		String flowBuilderServices = getFlowBuilderServicesAttribute(element);
		if (StringUtils.hasText(flowBuilderServices)) {
			definitionBuilder.addPropertyReference(FLOW_BUILDER_SERVICES_PROPERTY, flowBuilderServices);
		} else {
			definitionBuilder.addPropertyReference(FLOW_BUILDER_SERVICES_PROPERTY,
					FlowBuilderServicesBeanDefinitionParser.registerDefaultFlowBuilderServicesBeanDefinition(
							parserContext).getBeanName());
		}
		definitionBuilder.addPropertyValue(FLOW_LOCATIONS_PROPERTY, parseLocations(element));
		definitionBuilder.addPropertyValue(FLOW_BUILDERS_PROPERTY, parseFlowBuilders(element));
	}

	private List parseLocations(Element element) {
		List locationElements = DomUtils.getChildElementsByTagName(element, FLOW_LOCATION_ELEMENT);
		if (locationElements.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List locations = new ArrayList(locationElements.size());
		for (Iterator it = locationElements.iterator(); it.hasNext();) {
			Element locationElement = (Element) it.next();
			String id = locationElement.getAttribute(ID_ATTRIBUTE);
			String path = locationElement.getAttribute(PATH_ATTRIBUTE);
			locations.add(new FlowLocation(id, path, parseAttributes(locationElement)));
		}
		return locations;
	}

	private Set parseAttributes(Element element) {
		Element definitionAttributesElement = DomUtils.getChildElementByTagName(element, DEFINITION_ATTRIBUTES_ELEMENT);
		if (definitionAttributesElement != null) {
			List attributeElements = DomUtils.getChildElementsByTagName(definitionAttributesElement, ATTRIBUTE_ELEMENT);
			HashSet attributes = new HashSet(attributeElements.size());
			for (Iterator it = attributeElements.iterator(); it.hasNext();) {
				Element attributeElement = (Element) it.next();
				String name = attributeElement.getAttribute(NAME_ATTRIBUTE);
				String value = attributeElement.getAttribute(VALUE_ATTRIBUTE);
				String type = attributeElement.getAttribute(TYPE_ATTRIBUTE);
				attributes.add(new FlowElementAttribute(name, value, type));
			}
			return attributes;
		} else {
			return null;
		}
	}

	private List parseFlowBuilders(Element element) {
		List builderElements = DomUtils.getChildElementsByTagName(element, FLOW_BUILDER_ELEMENT);
		if (builderElements.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List builders = new ArrayList(builderElements.size());
		for (Iterator it = builderElements.iterator(); it.hasNext();) {
			Element builderElement = (Element) it.next();
			String id = builderElement.getAttribute(ID_ATTRIBUTE);
			String className = builderElement.getAttribute(CLASS_ATTRIBUTE);
			builders.add(new FlowBuilderInfo(id, className, parseAttributes(builderElement)));
		}
		return builders;
	}

	private String getFlowBuilderServicesAttribute(Element element) {
		return element.getAttribute(FLOW_BUILDER_SERVICES_ATTRIBUTE);
	}
}
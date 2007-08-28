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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.webflow.definition.registry.FlowDefinitionResource;
import org.springframework.webflow.engine.builder.FlowRegistryFactoryBean;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;registry&gt;</code> tag.
 * 
 * @author Ben Hale
 */
class RegistryBeanDefinitionParser extends AbstractBeanDefinitionParser {

	// elements
	private static final String LOCATION_ELEMENT = "location";

	private static final String NAMESPACE_ELEMENT = "namespace";

	// attributes
	private static final String ID_ATTRIBUTE = "id";

	private static final String NAME_ATTRIBUTE = "name";

	private static final String PATH_ATTRIBUTE = "path";

	// Properties
	private static final String XML_NAMESPACE_FLOW_MAPPINGS_PROPERTY = "xmlNamespaceFlowMappings";

	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
				.rootBeanDefinition(FlowRegistryFactoryBean.class);
		definitionBuilder.setSource(parserContext.extractSource(element));
		parseXml(element, definitionBuilder, parserContext);
		return definitionBuilder.getBeanDefinition();
	}

	public void parseXml(Element element, BeanDefinitionBuilder definitionBuilder, ParserContext parserContext) {
		Map xmlNamespaceFlowMappings = new HashMap();
		xmlNamespaceFlowMappings.put("", parseXmlElements(
				DomUtils.getChildElementsByTagName(element, LOCATION_ELEMENT), parserContext));
		List namespaceElements = DomUtils.getChildElementsByTagName(element, NAMESPACE_ELEMENT);
		for (Iterator it = namespaceElements.iterator(); it.hasNext();) {
			Element namespaceElement = (Element) it.next();
			String namespace = namespaceElement.getAttribute(NAME_ATTRIBUTE);
			xmlNamespaceFlowMappings.put(namespace, parseXmlElements(DomUtils.getChildElementsByTagName(
					namespaceElement, LOCATION_ELEMENT), parserContext));
		}
		definitionBuilder.addPropertyValue(XML_NAMESPACE_FLOW_MAPPINGS_PROPERTY, xmlNamespaceFlowMappings);
	}

	private Set parseXmlElements(List locationElements, ParserContext parserContext) {
		ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(parserContext
				.getReaderContext().getResourceLoader());
		Set resources = new HashSet();
		for (Iterator it = locationElements.iterator(); it.hasNext();) {
			Element locationElement = (Element) it.next();
			try {
				Resource[] locations = resolver.getResources(locationElement.getAttribute(PATH_ATTRIBUTE));
				if (locationElement.hasAttribute(ID_ATTRIBUTE)) {
					if (locations.length != 1) {
						parserContext.getReaderContext().error(
								"The 'path' attribute of the 'location' element must point to a single value "
										+ "flow definition if an id has been specified", locationElement);
					} else {
						resources.add(new FlowDefinitionResource(locationElement.getAttribute(ID_ATTRIBUTE),
								locations[0]));
					}
				} else {
					for (int i = 0; i < locations.length; i++) {
						resources.add(new FlowDefinitionResource(locations[i]));
					}
				}
			} catch (IOException e) {
				parserContext.getReaderContext().error(
						"The 'path' attribute of the 'location' element must point to a valid flow definition "
								+ "or definitions", locationElement);
			}
		}
		return resources;
	}
}
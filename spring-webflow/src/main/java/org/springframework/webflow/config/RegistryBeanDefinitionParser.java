/*
 * Copyright 2002-2007 the original author or authors.
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
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.webflow.engine.builder.xml.XmlFlowRegistryFactoryBean;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;registry&gt;</code> tag.
 * 
 * @author Ben Hale
 */
class RegistryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	// elements and attributes

	private static final String LOCATION_ELEMENT = "location";

	// properties

	private static final String FLOW_LOCATIONS_PROPERTY = "flowLocations";

	private static final String PATH_ATTRIBUTE = "path";

	protected Class getBeanClass(Element element) {
		return XmlFlowRegistryFactoryBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder definitionBuilder) {
		List locationElements = DomUtils.getChildElementsByTagName(element, LOCATION_ELEMENT);
		List locations = getLocations(locationElements);
		definitionBuilder.addPropertyValue(FLOW_LOCATIONS_PROPERTY, locations.toArray(new String[locations.size()]));
	}

	/**
	 * Parse location definitions from given list of location elements.
	 */
	private List getLocations(List locationElements) {
		List locations = new ArrayList(locationElements.size());
		for (Iterator i = locationElements.iterator(); i.hasNext();) {
			Element locationElement = (Element)i.next();
			String path = locationElement.getAttribute(PATH_ATTRIBUTE);
			if (StringUtils.hasText(path)) {
				locations.add(path);
			}
		}
		return locations;
	}
}
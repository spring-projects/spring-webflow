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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;execution-attributes&gt;</code> tag.
 * 
 * @author Ben Hale
 */
class ExecutionAttributesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	// elements and attributes

	private static final String ATTRIBUTE_ELEMENT = "attribute";

	private static final String NAME_ATTRIBUTE = "name";

	private static final String TYPE_ATTRIBUTE = "type";

	private static final String VALUE_ATTRIBUTE = "value";

	// properties

	private static final String SOURCE_MAP_PROPERTY = "sourceMap";

	protected Class getBeanClass(Element element) {
		return MapFactoryBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder definitionBuilder) {
		List attributeElements = DomUtils.getChildElementsByTagName(element, ATTRIBUTE_ELEMENT);
		Map attributeMap = new ManagedMap(attributeElements.size());
		putAttributes(attributeMap, attributeElements);
		putSpecialAttributes(attributeMap, element);
		definitionBuilder.addPropertyValue(SOURCE_MAP_PROPERTY, attributeMap);
	}

	/**
	 * Add all attributes defined in given list of attribute elements to given map.
	 */
	private void putAttributes(Map attributeMap, List attributeElements) {
		for (Iterator i = attributeElements.iterator(); i.hasNext();) {
			Element attributeElement = (Element)i.next();
			String type = attributeElement.getAttribute(TYPE_ATTRIBUTE);
			Object value;
			if (StringUtils.hasText(type)) {
				value = new TypedStringValue(attributeElement.getAttribute(VALUE_ATTRIBUTE), type);
			} else {
				value = attributeElement.getAttribute(VALUE_ATTRIBUTE);
			}
			attributeMap.put(attributeElement.getAttribute(NAME_ATTRIBUTE), value);
		}
	}

	/**
	 * Add all non-generic (special) attributes defined in given element
	 * to given map.
	 */
	private void putSpecialAttributes(Map attributeMap, Element element) {
		putAlwaysRedirectOnPauseAttribute(attributeMap,
				DomUtils.getChildElementByTagName(element, ApplicationViewSelector.ALWAYS_REDIRECT_ON_PAUSE_ATTRIBUTE));
	}

	/**
	 * Parse the "alwaysRedirectOnPause" attribute from given element and
	 * add it to given map.
	 */
	private void putAlwaysRedirectOnPauseAttribute(Map attributeMap, Element element) {
		if (element != null) {
			Boolean value = Boolean.valueOf(element.getAttribute(VALUE_ATTRIBUTE));
			attributeMap.put(ApplicationViewSelector.ALWAYS_REDIRECT_ON_PAUSE_ATTRIBUTE, value);
		}
	}
}
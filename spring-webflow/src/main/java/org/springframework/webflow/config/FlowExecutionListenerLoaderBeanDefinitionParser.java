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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;execution-listeners&gt;</code> tag.
 * 
 * @author Ben Hale
 */
class FlowExecutionListenerLoaderBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	// elements and attributes

	private static final String LISTENER_ELEMENT = "listener";

	private static final String CRITERIA_ATTRIBUTE = "criteria";

	private static final String REF_ATTRIBUTE = "ref";

	// properties

	private static final String LISTENERS_PROPERTY = "listeners";

	protected Class getBeanClass(Element element) {
		return FlowExecutionListenerLoaderFactoryBean.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder definitionBuilder) {
		List listenerElements = DomUtils.getChildElementsByTagName(element, LISTENER_ELEMENT);
		definitionBuilder.addPropertyValue(LISTENERS_PROPERTY, getListenersWithCriteria(listenerElements));
	}

	/**
	 * Creates a map of listeners with their associated criteria.
	 * @param listeners the list of listener elements from the bean definition
	 * @return a map containing keys that are references to given listeners and values of string that represent the
	 * criteria
	 */
	private Map getListenersWithCriteria(List listeners) {
		Map listenersWithCriteria = new ManagedMap(listeners.size());
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			Element listenerElement = (Element) i.next();
			RuntimeBeanReference ref = new RuntimeBeanReference(listenerElement.getAttribute(REF_ATTRIBUTE));
			String criteria = listenerElement.getAttribute(CRITERIA_ATTRIBUTE);
			listenersWithCriteria.put(ref, criteria);
		}
		return listenersWithCriteria;
	}
}
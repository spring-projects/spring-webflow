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
 * {@link BeanDefinitionParser} for the <code>&lt;flow-executor&gt;</code> tag.
 * 
 * @author Keith Donald
 */
class FlowExecutorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	// elements and attributes

	private static final String CONVERSATION_MANAGER_REF_ATTRIBUTE = "conversation-manager";

	private static final String EXECUTION_ATTRIBUTES_ELEMENT = "flow-execution-attributes";

	private static final String ALWAYS_REDIRECT_ON_PAUSE_ELEMENT = "alwaysRedirectOnPause";

	private static final String ATTRIBUTE_ELEMENT = "attribute";

	private static final String NAME_ATTRIBUTE = "name";

	private static final String VALUE_ATTRIBUTE = "value";

	private static final String TYPE_ATTRIBUTE = "type";

	private static final String EXECUTION_LISTENERS_ELEMENT = "flow-execution-listeners";

	private static final String MAX_CONTINUATIONS_ATTRIBUTE = "max-continuations";

	private static final String MAX_CONVERSATIONS_ATTRIBUTE = "max-conversations";

	private static final String REGISTRY_REF_ATTRIBUTE = "flow-registry";

	private static final String REPOSITORY_ELEMENT = "flow-execution-repository";

	// properties

	private static final String CONVERSATION_MANAGER_PROPERTY = "conversationManager";

	private static final String DEFINITION_LOCATOR_PROPERTY = "flowDefinitionLocator";

	private static final String REPOSITORY_TYPE_PROPERTY = "flowExecutionRepositoryType";

	private static final String EXECUTION_ATTRIBUTES_PROPERTY = "flowExecutionAttributes";

	private static final String EXECUTION_LISTENER_LOADER_PROPERTY = "flowExecutionListenerLoader";

	private static final String MAX_CONTINUATIONS_PROPERTY = "maxContinuations";

	private static final String MAX_CONVERSATIONS_PROPERTY = "maxConversations";

	protected Class getBeanClass(Element element) {
		return FlowExecutorFactoryBean.class;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder definitionBuilder) {
		definitionBuilder.addPropertyReference(DEFINITION_LOCATOR_PROPERTY, getRegistryRef(element, parserContext));
		definitionBuilder.addPropertyValue(EXECUTION_ATTRIBUTES_PROPERTY, parseAttributes(element));
		addExecutionListenerLoader(element, parserContext, definitionBuilder);
		configureRepository(element, definitionBuilder, parserContext);
	}

	/**
	 * Configures a repository based on the <code>repository-type</code> attribute or a <code>repository</code> tag.
	 * @param element the root element to extract repository configuration from
	 * @param definitionBuilder the builder
	 * @param parserContext the parserContext
	 */
	private void configureRepository(Element element, BeanDefinitionBuilder definitionBuilder,
			ParserContext parserContext) {
		Element repositoryElement = DomUtils.getChildElementByTagName(element, REPOSITORY_ELEMENT);
		if (repositoryElement != null) {
			definitionBuilder.addPropertyValue(REPOSITORY_TYPE_PROPERTY, getType(repositoryElement));
			configureContinuations(repositoryElement, definitionBuilder, parserContext);
			configureConversationManager(repositoryElement, definitionBuilder, parserContext);
		}
	}

	/**
	 * Configure the max continuations setting.
	 * @param repositoryElement the repository element
	 * @param definitionBuilder the builder
	 * @param parserContext the parserContext
	 */
	private void configureContinuations(Element repositoryElement, BeanDefinitionBuilder definitionBuilder,
			ParserContext parserContext) {
		String maxContinuations = getMaxContinuations(repositoryElement);
		if (StringUtils.hasText(maxContinuations)) {
			if (!getType(repositoryElement).equals("CONTINUATION")) {
				parserContext.getReaderContext().error(
						"The 'max-continuations' attribute of the 'repository' element must not "
								+ "have a value if the 'type' attribute is not 'continuation'", repositoryElement);
			}
			definitionBuilder.addPropertyValue(MAX_CONTINUATIONS_PROPERTY, maxContinuations);
		}
	}

	/**
	 * Configure the conversation manager
	 * @param repositoryElement the repository element
	 * @param definitionBuilder the builder
	 * @param parserContext the parserContext
	 */
	private void configureConversationManager(Element repositoryElement, BeanDefinitionBuilder definitionBuilder,
			ParserContext parserContext) {
		String conversationManagerRef = getConversationManagerRef(repositoryElement);
		String maxConversations = getMaxConversations(repositoryElement);
		if (StringUtils.hasText(conversationManagerRef)) {
			if (StringUtils.hasText(maxConversations)) {
				parserContext.getReaderContext().error(
						"The 'max-conversations' attribute of the 'repository' element must not "
								+ "have a value if there is a value for the 'conversation-manager-ref' attribute",
						repositoryElement);
			}
			definitionBuilder.addPropertyReference(CONVERSATION_MANAGER_PROPERTY, conversationManagerRef);
		} else if (StringUtils.hasText(maxConversations)) {
			definitionBuilder.addPropertyValue(MAX_CONVERSATIONS_PROPERTY, maxConversations);
		}
	}

	/**
	 * Returns the name of the registry detailed in the bean definition.
	 * @param element the element to extract the registry name from
	 * @return the name of the registry
	 * @param parserContext the parserContext
	 */
	private String getRegistryRef(Element element, ParserContext parserContext) {
		String registryRef = element.getAttribute(REGISTRY_REF_ATTRIBUTE);
		if (!StringUtils.hasText(registryRef)) {
			parserContext.getReaderContext().error(
					"The 'registry-ref' attribute of the 'flow-executor' element must have a value", element);
		}
		return registryRef;
	}

	/**
	 * Returns the name of the repository type enum field detailed in the bean definition.
	 * @param element the element to extract the repository type from
	 * @return the type of the repository
	 */
	private String getType(Element element) {
		return element.getAttribute(TYPE_ATTRIBUTE).toUpperCase();
	}

	/**
	 * Returns the maximum number of continuations detailed in the bean definition.
	 * @param element the element to extract the max continuations from
	 * @return the max continuations
	 */
	private String getMaxContinuations(Element element) {
		return element.getAttribute(MAX_CONTINUATIONS_ATTRIBUTE);
	}

	/**
	 * Returns the maximum number of conversations detailed in the bean definition.
	 * @param element the element to extract the max conversations from
	 * @return the max conversations
	 */
	private String getMaxConversations(Element element) {
		return element.getAttribute(MAX_CONVERSATIONS_ATTRIBUTE);
	}

	/**
	 * Returns the name of the conversation manager detailed in the bean definition.
	 * @param element the element to extract the conversation manager name from
	 * @return the name of the conversation manager
	 */
	private String getConversationManagerRef(Element element) {
		return element.getAttribute(CONVERSATION_MANAGER_REF_ATTRIBUTE);
	}

	/**
	 * Parse execution listener definitions contained in given element.
	 */
	private void addExecutionListenerLoader(Element element, ParserContext parserContext,
			BeanDefinitionBuilder definitionBuilder) {
		Element listenersElement = DomUtils.getChildElementByTagName(element, EXECUTION_LISTENERS_ELEMENT);
		if (listenersElement != null) {
			definitionBuilder.addPropertyValue(EXECUTION_LISTENER_LOADER_PROPERTY, parserContext.getDelegate()
					.parseCustomElement(listenersElement, definitionBuilder.getBeanDefinition()));
		}
	}

	private Set parseAttributes(Element element) {
		Element executionAttributesElement = DomUtils.getChildElementByTagName(element, EXECUTION_ATTRIBUTES_ELEMENT);
		if (executionAttributesElement != null) {
			HashSet attributes = new HashSet();
			Element redirectElement = DomUtils.getChildElementByTagName(executionAttributesElement,
					ALWAYS_REDIRECT_ON_PAUSE_ELEMENT);
			if (redirectElement != null) {
				String value = redirectElement.getAttribute(VALUE_ATTRIBUTE);
				attributes.add(new FlowElementAttribute("alwaysRedirectOnPause", value, "boolean"));
			}
			List attributeElements = DomUtils.getChildElementsByTagName(executionAttributesElement, ATTRIBUTE_ELEMENT);
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
}
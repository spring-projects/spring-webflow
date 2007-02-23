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

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;executor&gt;</code> tag.
 * 
 * @author Ben Hale
 * @author Christian Dupuis
 */
class ExecutorBeanDefinitionParser extends AbstractBeanDefinitionParser {

	// elements and attributes
	
	private static final String CONVERSATION_MANAGER_REF_ATTRIBUTE = "conversation-manager-ref";
	
	private static final String EXECUTION_ATTRIBUTES_ELEMENT = "execution-attributes";

	private static final String EXECUTION_LISTENERS_ELEMENT = "execution-listeners";
	
	private static final String MAX_CONTINUATIONS_ATTRIBUTE = "max-continuations";
	
	private static final String MAX_CONVERSATIONS_ATTRIBUTE = "max-conversations";

	private static final String REGISTRY_REF_ATTRIBUTE = "registry-ref";
	
	private static final String REPOSITORY_ELEMENT = "repository";

	private static final String REPOSITORY_TYPE_ATTRIBUTE = "repository-type";
	
	private static final String TYPE_ATTRIBUTE = "type";

	// properties

	private static final String CONVERSATION_MANAGER_PROPERTY = "conversationManager";
	
	private static final String DEFINITION_LOCATOR_PROPERTY = "definitionLocator";

	private static final String EXECUTION_ATTRIBUTES_PROPERTY = "executionAttributes";

	private static final String EXECUTION_LISTENER_LOADER_PROPERTY = "executionListenerLoader";
	
	private static final String MAX_CONTINUATIONS_PROPERTY = "maxContinuations";
	
	private static final String MAX_CONVERSATIONS_PROPERTY = "maxConversations";

	private static final String REPOSITORY_TYPE_PROPERTY = "repositoryType";
	

	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
				.rootBeanDefinition(FlowExecutorFactoryBean.class);
		definitionBuilder.setSource(parserContext.extractSource(element));
		definitionBuilder.addPropertyReference(DEFINITION_LOCATOR_PROPERTY, 
				getRegistryRef(element, parserContext));
		addExecutionAttributes(element, parserContext, definitionBuilder);
		addExecutionListenerLoader(element, parserContext, definitionBuilder);
		configureRepository(element, definitionBuilder, parserContext);
		return definitionBuilder.getBeanDefinition();
	}

	/**
	 * Configures a repository based on the <code>repository-type</code> attribute
	 * or a <code>repository</code> tag.
	 * @param element the root element to extract repository configuration from
	 * @param definitionBuilder the builder
	 * @param parserContext the parserContext
	 */
	private void configureRepository(Element element, BeanDefinitionBuilder definitionBuilder, 
			ParserContext parserContext) {
		Element repositoryElement = DomUtils.getChildElementByTagName(element, REPOSITORY_ELEMENT);
		String repositoryTypeAttribute = getRepositoryType(element);
		if (repositoryElement != null) {
			if (StringUtils.hasText(repositoryTypeAttribute)) {
				parserContext.getReaderContext().error(
					"The 'repositoryType' attribute of the 'executor' element must " +
					"not have a value if there is a 'repository' element", element);
			}
			definitionBuilder.addPropertyValue(REPOSITORY_TYPE_PROPERTY, getType(repositoryElement));
			configureContinuations(repositoryElement, definitionBuilder, parserContext);
			configureConversationManager(repositoryElement, definitionBuilder, parserContext);
		}
		else if (StringUtils.hasText(repositoryTypeAttribute)) {
			definitionBuilder.addPropertyValue(REPOSITORY_TYPE_PROPERTY, repositoryTypeAttribute);
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
						"The 'max-continuations' attribute of the 'repository' element must not " +
						"have a value if the 'type' attribute is not 'continuation'", repositoryElement);
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
			if(StringUtils.hasText(maxConversations)) {
				parserContext.getReaderContext().error(
						"The 'max-conversations' attribute of the 'repository' element must not " +
						"have a value if there is a value for the 'conversation-manager-ref' attribute", 
						repositoryElement);
			}
			definitionBuilder.addPropertyReference(CONVERSATION_MANAGER_PROPERTY, conversationManagerRef);
		}
		else if (StringUtils.hasText(maxConversations)) {
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
					"The 'registry-ref' attribute of the 'executor' element must have a value", element);
		}
		return registryRef;
	}

	/**
	 * Returns the name of the repository type enum field detailed in the bean
	 * definition.
	 * @param element the element to extract the repository type from
	 * @return the type of the repository
	 */
	private String getRepositoryType(Element element) {
		return element.getAttribute(REPOSITORY_TYPE_ATTRIBUTE).toUpperCase();
	}
	
	/**
	 * Returns the name of the repository type enum field detailed in the bean
	 * definition.
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
	 * Parse execution attribute definitions contained in given element.
	 */
	private void addExecutionAttributes(Element element, ParserContext parserContext,
			BeanDefinitionBuilder definitionBuilder) {
		Element attributesElement = DomUtils.getChildElementByTagName(element, EXECUTION_ATTRIBUTES_ELEMENT);
		if (attributesElement != null) {
			definitionBuilder.addPropertyValue(EXECUTION_ATTRIBUTES_PROPERTY, parserContext.getDelegate()
					.parseCustomElement(attributesElement, definitionBuilder.getBeanDefinition()));
		}
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
}
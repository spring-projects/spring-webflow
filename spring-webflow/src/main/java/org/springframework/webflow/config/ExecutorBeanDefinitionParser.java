/*
 * Copyright 2002-2006 the original author or authors.
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
import org.springframework.core.enums.StaticLabeledEnumResolver;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;executor&gt;</code> tag.
 * 
 * @author Ben Hale
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
		definitionBuilder.addPropertyReference(DEFINITION_LOCATOR_PROPERTY, getRegistryRef(element));
		addExecutionAttributes(element, parserContext, definitionBuilder);
		addExecutionListenerLoader(element, parserContext, definitionBuilder);
		configureRepository(element, definitionBuilder);
		return definitionBuilder.getBeanDefinition();
	}

	/**
	 * Configures a repository based on the <code>repositoryType</code> attribute
	 * or a <code>repository</code> tag.
	 * @param element the root element to extract repository configuration from
	 */
	private void configureRepository(Element element, BeanDefinitionBuilder definitionBuilder) {
		Element repositoryElement = DomUtils.getChildElementByTagName(element, REPOSITORY_ELEMENT);
		String repositoryTypeAttribute = getRepositoryType(element);
		if(repositoryElement != null) {
			if(StringUtils.hasText(repositoryTypeAttribute)) {
				throw new IllegalArgumentException("The 'repositoryType' attribute of the 'executor' element must not have a value if there is a 'repository' element");
			}
			definitionBuilder.addPropertyValue(REPOSITORY_TYPE_PROPERTY, getType(repositoryElement));
			configureContinuations(repositoryElement, definitionBuilder);
			configureConversationManager(repositoryElement, definitionBuilder);
		} else if (StringUtils.hasText(repositoryTypeAttribute)) {
			definitionBuilder.addPropertyValue(REPOSITORY_TYPE_PROPERTY, repositoryTypeAttribute);
		}	
	}

	private void configureConversationManager(Element repositoryElement, BeanDefinitionBuilder definitionBuilder) {
		String conversationManagerRef = getConversationManagerRef(repositoryElement);
		String maxConversations = getMaxConversations(repositoryElement);
		if(StringUtils.hasText(conversationManagerRef)) {
			if(StringUtils.hasText(maxConversations)) {
				throw new IllegalArgumentException("The 'maxConversations' attribute of the 'repository' element must not have a value if there is a value for the 'conversation-manager-ref' attribute");
			}
			definitionBuilder.addPropertyReference(CONVERSATION_MANAGER_PROPERTY, conversationManagerRef);
		} else if (StringUtils.hasText(maxConversations)) {
			definitionBuilder.addPropertyValue(MAX_CONVERSATIONS_PROPERTY, maxConversations);
		}	
	}
	
	private void configureContinuations(Element repositoryElement, BeanDefinitionBuilder definitionBuilder) {
		RepositoryType repositoryType = (RepositoryType) StaticLabeledEnumResolver.instance().getLabeledEnumByLabel(RepositoryType.class, getRepositoryType(repositoryElement));
		String maxContinuations = getMaxContinuations(repositoryElement);
		if(StringUtils.hasText(maxContinuations)) {
			if(repositoryType != RepositoryType.CONTINUATION) {
				throw new IllegalArgumentException("The 'maxContinuations' attribute of the 'repository' element must not have a value if the 'type' attribute is not 'continuation'");
			}
			definitionBuilder.addPropertyValue(MAX_CONTINUATIONS_PROPERTY, maxContinuations);
		}
	}

	/**
	 * Returns the name of the registry detailed in the bean definition.
	 * @param element the element to extract the registry name from
	 * @return the name of the registry
	 */
	private String getRegistryRef(Element element) {
		String registryRef = element.getAttribute(REGISTRY_REF_ATTRIBUTE);
		if (!StringUtils.hasText(registryRef)) {
			throw new IllegalArgumentException("The 'registry-ref' attribute of the 'executor' element must have a value");
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
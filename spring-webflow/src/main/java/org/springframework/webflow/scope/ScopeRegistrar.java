/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.Ordered;
import org.springframework.webflow.execution.ScopeType;

/**
 * Registers the Spring Web Flow bean scopes with a {@link ConfigurableListableBeanFactory}.
 * 
 * @author Ben Hale
 * @see Scope
 */
public class ScopeRegistrar implements BeanFactoryPostProcessor, Ordered {

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope(ScopeType.REQUEST.getLabel().toLowerCase(), new RequestScope());
		beanFactory.registerScope(ScopeType.FLASH.getLabel().toLowerCase(), new FlashScope());
		beanFactory.registerScope(ScopeType.VIEW.getLabel().toLowerCase(), new ViewScope());
		beanFactory.registerScope(ScopeType.FLOW.getLabel().toLowerCase(), new FlowScope());
		beanFactory.registerScope(ScopeType.CONVERSATION.getLabel().toLowerCase(), new ConversationScope());
	}

	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}

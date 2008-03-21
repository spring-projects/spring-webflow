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
package org.springframework.webflow.engine.builder;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;

/**
 * A builder context that delegates to a flow-local bean factory for builder services. Such builder services override
 * the services of the external "parent" context.
 * @author Keith Donald
 */
class LocalFlowBuilderContext implements FlowBuilderContext {

	private FlowBuilderContext parent;

	private GenericApplicationContext localFlowContext;

	public LocalFlowBuilderContext(FlowBuilderContext parent, GenericApplicationContext localFlowContext) {
		this.parent = parent;
		this.localFlowContext = localFlowContext;
	}

	public String getFlowId() {
		return parent.getFlowId();
	}

	public AttributeMap getFlowAttributes() {
		return parent.getFlowAttributes();
	}

	public FlowDefinitionLocator getFlowDefinitionLocator() {
		if (localFlowContext.containsLocalBean("flowRegistry")) {
			return (FlowDefinitionLocator) localFlowContext.getBean("flowRegistry", FlowDefinitionLocator.class);
		} else {
			return parent.getFlowDefinitionLocator();
		}
	}

	public FlowArtifactFactory getFlowArtifactFactory() {
		if (localFlowContext.containsLocalBean("flowArtifactFactory")) {
			return (FlowArtifactFactory) localFlowContext.getBean("flowArtifactFactory", FlowArtifactFactory.class);
		} else {
			return parent.getFlowArtifactFactory();
		}
	}

	public ConversionService getConversionService() {
		if (localFlowContext.containsLocalBean("conversionService")) {
			return (ConversionService) localFlowContext.getBean("conversionService", ConversionService.class);
		} else {
			return parent.getConversionService();
		}
	}

	public ViewFactoryCreator getViewFactoryCreator() {
		if (localFlowContext.containsLocalBean("viewFactoryCreator")) {
			return (ViewFactoryCreator) localFlowContext.getBean("viewFactoryCreator", ViewFactoryCreator.class);
		} else {
			return parent.getViewFactoryCreator();
		}
	}

	public FormatterRegistry getFormatterRegistry() {
		if (localFlowContext.containsLocalBean("formatterRegistry")) {
			return (FormatterRegistry) localFlowContext.getBean("formatterRegistry", FormatterRegistry.class);
		} else {
			return parent.getFormatterRegistry();
		}
	}

	public ExpressionParser getExpressionParser() {
		if (localFlowContext.containsLocalBean("expressionParser")) {
			return (ExpressionParser) localFlowContext.getBean("expressionParser", ExpressionParser.class);
		} else {
			return parent.getExpressionParser();
		}
	}

	public ResourceLoader getResourceLoader() {
		return localFlowContext;
	}

	public BeanFactory getBeanFactory() {
		return localFlowContext.getBeanFactory();
	}
}
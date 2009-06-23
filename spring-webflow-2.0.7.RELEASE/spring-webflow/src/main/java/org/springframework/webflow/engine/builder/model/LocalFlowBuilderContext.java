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
package org.springframework.webflow.engine.builder.model;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.builder.FlowArtifactFactory;
import org.springframework.webflow.engine.builder.FlowBuilderContext;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;

/**
 * A builder context that delegates to a flow-local bean factory for builder services. Such builder services override
 * the services of the external "parent" context.
 * @author Keith Donald
 */
class LocalFlowBuilderContext implements FlowBuilderContext {

	private FlowBuilderContext parent;

	private ApplicationContext localFlowContext;

	public LocalFlowBuilderContext(FlowBuilderContext parent, GenericApplicationContext localFlowContext) {
		this.parent = parent;
		this.localFlowContext = localFlowContext;
	}

	public ApplicationContext getApplicationContext() {
		return localFlowContext;
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

	public ExpressionParser getExpressionParser() {
		if (localFlowContext.containsLocalBean("expressionParser")) {
			return (ExpressionParser) localFlowContext.getBean("expressionParser", ExpressionParser.class);
		} else {
			return parent.getExpressionParser();
		}
	}

}
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
package org.springframework.webflow.engine.builder;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;

/**
 * Provides services needed to a direct a flow builder through building a flow definition.
 * @author Keith Donald
 */
public interface FlowBuilderContext {

	/**
	 * Returns an externally configured flow definition identifier to assign to the flow being built.
	 * @return the flow id
	 */
	public String getFlowId();

	/**
	 * Returns externally configured attributes to assign to the flow definition being built.
	 * @return the flow attributes
	 */
	public AttributeMap getFlowAttributes();

	/**
	 * Returns the locator for locating dependent flows (subflows).
	 * @return the flow definition locator
	 */
	public FlowDefinitionLocator getFlowDefinitionLocator();

	/**
	 * Returns the factory for core flow artifacts such as Flow and State.
	 * @return the flow artifact factory
	 */
	public FlowArtifactFactory getFlowArtifactFactory();

	/**
	 * Returns a generic type conversion service for converting between types, typically from string to a rich value
	 * object.
	 * @return the generic conversion service
	 */
	public ConversionService getConversionService();

	/**
	 * Returns the view factory creator for configuring a ViewFactory per view state
	 * @return the view factory creator
	 */
	public ViewFactoryCreator getViewFactoryCreator();

	/**
	 * Returns the expression parser for parsing expression strings.
	 * @return the expression parser
	 */
	public ExpressionParser getExpressionParser();

	/**
	 * Returns the application context hosting the flow system.
	 * @return the application context
	 */
	public ApplicationContext getApplicationContext();
}
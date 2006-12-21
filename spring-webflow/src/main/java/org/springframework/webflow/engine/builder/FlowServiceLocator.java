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
package org.springframework.webflow.engine.builder;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.core.io.ResourceLoader;
import org.springframework.webflow.action.AbstractBeanInvokingAction;
import org.springframework.webflow.action.BeanInvokingActionFactory;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowAttributeMapper;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.TargetStateResolver;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.TransitionCriteria;
import org.springframework.webflow.engine.ViewSelector;
import org.springframework.webflow.execution.Action;

/**
 * A support interface used by flow builders at configuration time. Acts as a
 * "service locator" responsible for:
 * <ol>
 * <li> Retrieving dependent (but externally managed) flow services needed to
 * configure flow and state definitions. Such services are usually hosted in a
 * backing registry and may be shared by multiple flows.
 * <li> Providing access to abstract factories to create core flow definitional
 * artifacts such as {@link Flow}, {@link State}, {@link Transition}, and
 * {@link AbstractBeanInvokingAction bean invoking actions}. These artifacts
 * are unique to each flow and are typically not shared.
 * </ol>
 * <p>
 * In general, implementations of this interface act as facades to accessing and
 * creating flow artifacts during {@link FlowAssembler flow assembly}.
 * <p>
 * Finally, this interface also exposes access to generic infrastructure
 * services also needed by flow assemblers such as a {@link ConversionService}
 * and {@link ExpressionParser}.
 * 
 * @see org.springframework.webflow.engine.builder.FlowBuilder
 * @see org.springframework.webflow.engine.builder.BaseFlowBuilder
 * @see org.springframework.webflow.engine.builder.FlowAssembler
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface FlowServiceLocator {

	/**
	 * Returns the Flow to be used as a subflow with the provided id.
	 * @param id the flow id
	 * @return the flow to be used as a subflow
	 * @throws FlowArtifactLookupException when no such flow is found
	 */
	public Flow getSubflow(String id) throws FlowArtifactLookupException;

	/**
	 * Retrieve an action to be executed within a flow with the assigned id.
	 * @param id the id of the action
	 * @throws FlowArtifactLookupException when no such action is found
	 */
	public Action getAction(String id) throws FlowArtifactLookupException;

	/**
	 * Returns the flow attribute mapper with the provided id. Flow attribute
	 * mappers are used from subflow states to map input and output attributes.
	 * @param id the attribute mapper id
	 * @return the attribute mapper
	 * @throws FlowArtifactLookupException when no such mapper is found
	 */
	public FlowAttributeMapper getAttributeMapper(String id) throws FlowArtifactLookupException;

	/**
	 * Returns the transition criteria to drive state transitions with the
	 * provided id.
	 * @param id the transition criteria id
	 * @return the transition criteria
	 * @throws FlowArtifactLookupException when no such criteria is found
	 */
	public TransitionCriteria getTransitionCriteria(String id) throws FlowArtifactLookupException;

	/**
	 * Returns the transition target state resolver with the specified id.
	 * @param id the target state resolver id
	 * @return the target state resolver
	 * @throws FlowArtifactLookupException when no such resolver is found
	 */
	public TargetStateResolver getTargetStateResolver(String id) throws FlowArtifactLookupException;

	/**
	 * Returns the view selector to make view selections in view states with the
	 * provided id.
	 * @param id the view selector id
	 * @return the view selector
	 * @throws FlowArtifactLookupException when no such selector is found
	 */
	public ViewSelector getViewSelector(String id) throws FlowArtifactLookupException;

	/**
	 * Returns the exception handler to handle flow execution exceptions with
	 * the provided id.
	 * @param id the exception handler id
	 * @return the exception handler
	 * @throws FlowArtifactLookupException when no such handler is found
	 */
	public FlowExecutionExceptionHandler getExceptionHandler(String id) throws FlowArtifactLookupException;

	/**
	 * Returns the factory for core flow artifacts such as Flow and State.
	 * @return the flow artifact factory
	 */
	public FlowArtifactFactory getFlowArtifactFactory();

	/**
	 * Returns the factory for bean invoking actions.
	 * @return the bean invoking action factory
	 */
	public BeanInvokingActionFactory getBeanInvokingActionFactory();

	/**
	 * Returns a generic bean (service) registry for accessing arbitrary beans.
	 * @return the generic service registry
	 * @throws UnsupportedOperationException when not supported by this locator
	 */
	public BeanFactory getBeanFactory() throws UnsupportedOperationException;

	/**
	 * Returns a generic resource loader for accessing file-based resources.
	 * @return the generic resource loader
	 */
	public ResourceLoader getResourceLoader();

	/**
	 * Returns the expression parser for parsing expression strings.
	 * @return the expression parser
	 */
	public ExpressionParser getExpressionParser();

	/**
	 * Returns a generic type conversion service for converting between types,
	 * typically from string to a rich value object.
	 * @return the generic conversion service
	 */
	public ConversionService getConversionService();
}
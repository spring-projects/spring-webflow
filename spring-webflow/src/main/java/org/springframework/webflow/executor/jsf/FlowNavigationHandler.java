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
package org.springframework.webflow.executor.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.web.jsf.DecoratingNavigationHandler;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.engine.NoMatchingTransitionException;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.executor.RequestParameterInputMapper;
import org.springframework.webflow.executor.support.FlowExecutorArgumentExtractor;

/**
 * An implementation of a JSF <code>NavigationHandler</code> that provides integration with Spring Web Flow.
 * Responsible for delegating to Spring Web Flow to launch and resume flow executions, treating JSF action outcomes
 * (like a command button click) as web flow events.
 * 
 * This class delegates to the standard NavigationHandler implementation when a navigation request does not pertain to a
 * flow execution.
 * <p>
 * The following navigation handler algorithm is implemented by default:
 * </p>
 * <p>
 * If a flow execution has been restored in the current request:
 * <ul>
 * <li>Resume the flow execution by signaling the JSF action outcome as an event against the current state.
 * <li>Once event processing completes expose the selected view as the "current" {@link ViewSelection}.
 * </ul>
 * </p>
 * <p>
 * If a flow execution has not been restored in the current request:
 * <ul>
 * <li>If the specified logical outcome is of the form <em>flowId:xxx</em> look up the corresponding
 * {@link FlowDefinition} with that id and launch a new flow execution in the starting state. Expose the new execution
 * as the "current" flow execution for this request. Expose the first selected view as the "current" view selection.
 * <li>If the specified logical outcome is not of the form <em>flowId:xxx</em>, simply delegate to the standard
 * <code>NavigationHandler</code> implementation and return.
 * </ul>
 * </p>
 * How the flowId and eventId arguments are extracted can be customized by setting a custom
 * {@link #setArgumentExtractor(FlowExecutorArgumentExtractor) argument extractor}.
 * 
 * Note about customization: since NavigationHandlers managed directly by the JSF provider cannot be benefit from
 * DependencyInjection, See Spring's {@link org.springframework.web.jsf.DelegatingNavigationHandlerProxy} when you need
 * to customize a FlowNavigationHandler instance.
 * 
 * @author Craig McClanahan
 * @author Colin Sampaleanu
 * @author Keith Donald
 */
public class FlowNavigationHandler extends DecoratingNavigationHandler {

	/**
	 * Logger, usable by subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * A helper for extracting parameters needed by this flow navigation handler.
	 */
	private FlowExecutorArgumentExtractor argumentExtractor = new FlowNavigationHandlerArgumentExtractor();

	/**
	 * The service responsible for mapping attributes of an {@link ExternalContext} to a new {@link FlowExecution}
	 * during the {@link #launch(String, ExternalContext) launch flow} operation.
	 * <p>
	 * This allows developers to control what attributes are made available in the <code>inputMap</code> to new
	 * top-level flow executions. The starting execution may then choose to map that available input into its own local
	 * scope.
	 * <p>
	 * The default implementation simply exposes all request parameters as flow execution input attributes. May be null.
	 */
	private AttributeMapper inputMapper = new RequestParameterInputMapper();

	/**
	 * Create a new {@link FlowNavigationHandler} using the default constructor.
	 */
	public FlowNavigationHandler() {
		super();
	}

	/**
	 * Create a new {@link FlowNavigationHandler}, wrapping the specified standard navigation handler implementation.
	 * @param originalNavigationHandler Standard <code>NavigationHandler</code> we are wrapping
	 */
	public FlowNavigationHandler(NavigationHandler originalNavigationHandler) {
		super(originalNavigationHandler);
	}

	/**
	 * Returns the argument extractor used by this navigation handler.
	 */
	public FlowExecutorArgumentExtractor getArgumentExtractor() {
		return argumentExtractor;
	}

	/**
	 * Sets the argument extractor to use by this navigation handler. Call to customize how flow id and event id
	 * arguments are extracted.
	 */
	public void setArgumentExtractor(FlowExecutorArgumentExtractor argumentExtractor) {
		this.argumentExtractor = argumentExtractor;
	}

	/**
	 * Returns the configured flow execution input mapper.
	 */
	public AttributeMapper getInputMapper() {
		return inputMapper;
	}

	/**
	 * Sets the service responsible for mapping attributes of an {@link ExternalContext} to a new {@link FlowExecution}
	 * during a launch flow operation.
	 * <p>
	 * The default implementation simply exposes all request parameters as flow execution input attributes. May be null.
	 * @see RequestParameterInputMapper
	 */
	public void setInputMapper(AttributeMapper inputMapper) {
		this.inputMapper = inputMapper;
	}

	public void handleNavigation(FacesContext facesContext, String fromAction, String outcome,
			NavigationHandler originalNavigationHandler) {
		try {
			JsfExternalContext context = getCurrentContext();
			// record the navigation handler context
			context.handleNavigationCalled(fromAction, outcome);
			// first see if we need to launch a new flow execution if the flow id is present
			if (argumentExtractor.isFlowIdPresent(context)) {
				// a flow execution launch has been requested - create the new execution
				String flowId = argumentExtractor.extractFlowId(context);
				FlowDefinition flowDefinition = getLocator(context).getFlowDefinition(flowId);
				FlowExecution flowExecution = getFactory(context).createFlowExecution(flowDefinition);
				// check to see if this execution was created while another was running
				if (FlowExecutionHolderUtils.isFlowExecutionRestored(facesContext)) {
					// replace the current flow execution with the new one
					FlowExecutionHolderUtils.getFlowExecutionHolder(facesContext).replaceWith(flowExecution);
				} else {
					// bind the new execution as the 'current execution'
					FlowExecutionHolderUtils.setFlowExecutionHolder(new FlowExecutionHolder(flowExecution),
							facesContext);
				}
				// start the new execution
				ViewSelection selectedView = flowExecution.start(createInput(context), context);
				// set the starting view to render
				FlowExecutionHolderUtils.getFlowExecutionHolder(facesContext).setViewSelection(selectedView);
			} else {
				// not a launch request - see if this is a resume request to continue an existing execution
				if (FlowExecutionHolderUtils.isFlowExecutionRestored(facesContext)) {
					// a flow execution has been restored - see if we need to signal an event against it
					if (argumentExtractor.isEventIdPresent(context)) {
						// signal the event against the current flow execution
						String eventId = argumentExtractor.extractEventId(context);
						try {
							FlowExecutionHolder holder = FlowExecutionHolderUtils.getFlowExecutionHolder(facesContext);
							ViewSelection selectedView = holder.getFlowExecution().signalEvent(eventId, context);
							// set the next view to render
							holder.setViewSelection(selectedView);
						} catch (NoMatchingTransitionException e) {
							if (logger.isDebugEnabled()) {
								logger.debug("No flow state transition found for event '" + eventId
										+ "'; falling back to standard navigation handler.");
							}
							// not a valid event in the current state: proceed with standard navigation
							originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
						}
					}
				} else {
					// neither a flow launch or resume request: proceed with standard navigation
					originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
				}
			}
		} catch (RuntimeException e) {
			cleanupResources(facesContext);
			throw e;
		} catch (Error e) {
			cleanupResources(facesContext);
			throw e;
		}
	}

	/**
	 * Factory method that creates the input attribute map for a newly created {@link FlowExecution}. This
	 * implementation uses the registered input mapper, if any.
	 * @param context the external context
	 * @return the input map, or null if no input
	 */
	protected MutableAttributeMap createInput(ExternalContext context) {
		if (inputMapper != null) {
			MutableAttributeMap inputMap = new LocalAttributeMap();
			inputMapper.map(context, inputMap, null);
			return inputMap;
		} else {
			return null;
		}
	}

	// helpers

	private JsfExternalContext getCurrentContext() {
		return (JsfExternalContext) ExternalContextHolder.getExternalContext();
	}

	private FlowDefinitionLocator getLocator(JsfExternalContext context) {
		return FlowFacesUtils.getDefinitionLocator(context.getFacesContext());
	}

	private FlowExecutionFactory getFactory(JsfExternalContext context) {
		return FlowFacesUtils.getExecutionFactory(context.getFacesContext());
	}

	private void cleanupResources(FacesContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("Cleaning up allocated flow system resources");
		}
		FlowExecutionHolderUtils.cleanupCurrentFlowExecution(context);
		ExternalContextHolder.setExternalContext(null);
	}
}
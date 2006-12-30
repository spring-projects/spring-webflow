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
package org.springframework.webflow.executor.jsf;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.execution.support.FlowExecutionRedirect;
import org.springframework.webflow.executor.support.FlowExecutorArgumentHandler;
import org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler;

/**
 * JSF phase listener that is responsible for managing a {@link FlowExecution}
 * object representing an active user conversation so that other JSF artifacts
 * that execute in different phases of the JSF lifecycle may have access to it.
 * <p>
 * This phase listener implements the following algorithm:
 * <ul>
 * <li>On BEFORE_RESTORE_VIEW, restore the {@link FlowExecution} the user is
 * participating in if a call to
 * {@link FlowExecutorArgumentHandler#extractFlowExecutionKey(ExternalContext)}
 * returns a submitted flow execution identifier. Place the restored flow
 * execution in a holder that other JSF artifacts such as VariableResolvers,
 * PropertyResolvers, and NavigationHandlers may access during the request
 * lifecycle.
 * <li>On BEFORE_RENDER_RESPONSE, if a flow execution was restored in the
 * RESTORE_VIEW phase generate a new key for identifying the updated execution
 * within a the selected {@link FlowExecutionRepository}. Expose managed flow
 * execution attributes to the views before rendering.
 * <li>On AFTER_RENDER_RESPONSE, if a flow execution was restored in the
 * RESTORE_VIEW phase <em>save</em> the updated execution to the repository
 * using the new key generated in the BEFORE_RENDER_RESPONSE phase.
 * </ul>
 * 
 * @author Colin Sampaleanu
 * @author Keith Donald
 */
public class FlowPhaseListener implements PhaseListener {

	/**
	 * Logger, usable by subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * A helper for handling arguments needed by this phase listener.
	 */
	private FlowExecutorArgumentHandler argumentHandler = new RequestParameterFlowExecutorArgumentHandler();

	/**
	 * Resolves selected Web Flow view names to JSF view ids.
	 */
	private ViewIdMapper viewIdMapper = new DefaultViewIdMapper();

	/**
	 * Returns the argument handler used by this phase listener.
	 */
	public FlowExecutorArgumentHandler getArgumentHandler() {
		return argumentHandler;
	}

	/**
	 * Sets the argument handler to use.
	 */
	public void setArgumentHandler(FlowExecutorArgumentHandler argumentHandler) {
		this.argumentHandler = argumentHandler;
	}

	/**
	 * Returns the JSF view id resolver used by this phase listener.
	 */
	public ViewIdMapper getViewIdMapper() {
		return viewIdMapper;
	}

	/**
	 * Sets the JSF view id mapper used by this phase listener.
	 */
	public void setViewIdMapper(ViewIdMapper viewIdMapper) {
		this.viewIdMapper = viewIdMapper;
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	public void beforePhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
			ExternalContextHolder.setExternalContext(new JsfExternalContext(event.getFacesContext()));
			restoreFlowExecution(event.getFacesContext());
		}
		else if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			if (FlowExecutionHolderUtils.isFlowExecutionRestored(event.getFacesContext())) {
				prepareResponse(getCurrentContext(), FlowExecutionHolderUtils.getFlowExecutionHolder(event
						.getFacesContext()));
			}
		}
	}

	public void afterPhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			try {
				if (FlowExecutionHolderUtils.isFlowExecutionChanged(event.getFacesContext())) {
					saveFlowExecution(getCurrentContext(), FlowExecutionHolderUtils.getFlowExecutionHolder(event
							.getFacesContext()));
				}
			}
			finally {
				ExternalContextHolder.setExternalContext(null);
			}
		}
	}

	private JsfExternalContext getCurrentContext() {
		return (JsfExternalContext) ExternalContextHolder.getExternalContext();
	}

	protected void restoreFlowExecution(FacesContext facesContext) {
		JsfExternalContext context = new JsfExternalContext(facesContext);
		if (argumentHandler.isFlowExecutionKeyPresent(context)) {
			// restore flow execution from repository so it will be
			// available to variable/property resolvers and the flow
			// navigation handler (this could happen as part of a submission or
			// flow execution redirect)
			FlowExecutionRepository repository = getRepository(context);
			FlowExecutionKey flowExecutionKey = repository.parseFlowExecutionKey(argumentHandler
					.extractFlowExecutionKey(context));
			FlowExecution flowExecution = repository.getFlowExecution(flowExecutionKey);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded existing flow execution from repository with id '" + flowExecutionKey + "'");
			}
			FlowExecutionHolderUtils.setFlowExecutionHolder(new FlowExecutionHolder(flowExecutionKey, flowExecution),
					facesContext);
		}
		else if (argumentHandler.isFlowIdPresent(context)) {
			// launch a new flow execution (this could happen as part of a flow
			// redirect)
			String flowId = argumentHandler.extractFlowId(context);
			FlowDefinition flowDefinition = getLocator(context).getFlowDefinition(flowId);
			FlowExecution flowExecution = getFactory(context).createFlowExecution(flowDefinition);
			FlowExecutionHolder holder = new FlowExecutionHolder(flowExecution);
			FlowExecutionHolderUtils.setFlowExecutionHolder(holder, facesContext);
			ViewSelection selectedView = flowExecution.start(createInput(flowExecution, context), context);
			if (logger.isDebugEnabled()) {
				logger.debug("Started new flow execution");
			}
			holder.setViewSelection(selectedView);
			holder.markNeedsSave();
		}
	}

	/**
	 * Factory method that creates the input attribute map for a newly created
	 * {@link FlowExecution}. TODO - add support for input mappings here
	 * @param flowExecution the new flow execution (yet to be started)
	 * @param context the external context
	 * @return the input map
	 */
	protected LocalAttributeMap createInput(FlowExecution flowExecution, ExternalContext context) {
		return null;
	}

	protected void prepareResponse(JsfExternalContext context, FlowExecutionHolder holder) {
		if (holder.needsSave()) {
			generateKey(context, holder);
		}
		ViewSelection selectedView = holder.getViewSelection();
		if (selectedView == null) {
			selectedView = holder.getFlowExecution().refresh(context);
			holder.setViewSelection(selectedView);
		}
		if (selectedView instanceof ApplicationView) {
			prepareApplicationView(context.getFacesContext(), holder);
		}
		else if (selectedView instanceof FlowExecutionRedirect) {
			String url = argumentHandler.createFlowExecutionUrl(holder.getFlowExecutionKey().toString(), holder
					.getFlowExecution(), context);
			sendRedirect(url, context);
		}
		else if (selectedView instanceof ExternalRedirect) {
			String flowExecutionKey = holder.getFlowExecution().isActive() ? holder.getFlowExecutionKey().toString()
					: null;
			String url = argumentHandler.createExternalUrl((ExternalRedirect) holder.getViewSelection(),
					flowExecutionKey, context);
			sendRedirect(url, context);
		}
		else if (selectedView instanceof FlowDefinitionRedirect) {
			String url = argumentHandler.createFlowDefinitionUrl((FlowDefinitionRedirect) holder.getViewSelection(),
					context);
			sendRedirect(url, context);
		}
	}

	protected void prepareApplicationView(FacesContext facesContext, FlowExecutionHolder holder) {
		ApplicationView forward = (ApplicationView) holder.getViewSelection();
		if (forward != null) {
			putInto(facesContext.getExternalContext().getRequestMap(), forward.getModel());
			updateViewRoot(facesContext, viewIdMapper.mapViewId(forward.getViewName()));
		}
		Map requestMap = facesContext.getExternalContext().getRequestMap();
		argumentHandler.exposeFlowExecutionContext(holder.getFlowExecutionKey().toString(), holder.getFlowExecution(),
				requestMap);
	}

	private void updateViewRoot(FacesContext facesContext, String viewId) {
		UIViewRoot viewRoot = facesContext.getViewRoot();
		if (viewRoot == null || hasViewChanged(viewRoot, viewId)) {
			// create the specified view so that it can be rendered
			ViewHandler handler = facesContext.getApplication().getViewHandler();
			UIViewRoot view = handler.createView(facesContext, viewId);
			facesContext.setViewRoot(view);
		}
	}

	private boolean hasViewChanged(UIViewRoot viewRoot, String viewId) {
		return !viewRoot.getViewId().equals(viewId);
	}

	private void generateKey(JsfExternalContext context, FlowExecutionHolder holder) {
		FlowExecution flowExecution = holder.getFlowExecution();
		if (flowExecution.isActive()) {
			// generate new continuation key for the flow execution
			// before rendering the response
			FlowExecutionKey flowExecutionKey = holder.getFlowExecutionKey();
			FlowExecutionRepository repository = getRepository(context);
			if (flowExecutionKey == null) {
				// it is an new conversation, generate a brand new key
				flowExecutionKey = repository.generateKey(flowExecution);
			}
			else {
				// it is an existing conversaiton, use same conversation id,
				// generate a new continuation id
				flowExecutionKey = repository.getNextKey(flowExecution, flowExecutionKey);
			}
			holder.setFlowExecutionKey(flowExecutionKey);
		}
	}

	protected void saveFlowExecution(JsfExternalContext context, FlowExecutionHolder holder) {
		FlowExecution flowExecution = holder.getFlowExecution();
		FlowExecutionRepository repository = getRepository(context);
		if (flowExecution.isActive()) {
			// save the flow execution out to the repository
			if (logger.isDebugEnabled()) {
				logger.debug("Saving continuation to repository with key " + holder.getFlowExecutionKey());
			}
			repository.putFlowExecution(holder.getFlowExecutionKey(), flowExecution);
		}
		else {
			if (holder.getFlowExecutionKey() != null) {
				// remove the flow execution from the repository
				if (logger.isDebugEnabled()) {
					logger.debug("Removing execution in repository with key '" + holder.getFlowExecutionKey() + "'");
				}
				repository.removeFlowExecution(holder.getFlowExecutionKey());
			}
		}
	}

	/**
	 * Utility method needed needed only because we can not rely on JSF
	 * RequestMap supporting Map's putAll method. Tries putAll, falls back to
	 * individual adds
	 * @param targetMap the target map to add the model data to
	 * @param map the model data to add to the target map
	 */
	private void putInto(Map targetMap, Map map) {
		try {
			targetMap.putAll(map);
		}
		catch (UnsupportedOperationException e) {
			// work around nasty MyFaces bug where it's RequestMap doesn't
			// support putAll remove after it's fixed in MyFaces
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				targetMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	private void sendRedirect(String url, JsfExternalContext context) {
		try {
			context.getFacesContext().getExternalContext().redirect(url);
			context.getFacesContext().responseComplete();
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not send redirect to " + url);
		}
	}

	/**
	 * Standard default view id resolver which uses the web flow view name as
	 * the jsf view id
	 */
	public static class DefaultViewIdMapper implements ViewIdMapper {
		public String mapViewId(String viewName) {
			return viewName;
		}
	}

	private FlowDefinitionLocator getLocator(JsfExternalContext context) {
		return FlowFacesUtils.getDefinitionLocator(context.getFacesContext());
	}

	private FlowExecutionFactory getFactory(JsfExternalContext context) {
		return FlowFacesUtils.getExecutionFactory(context.getFacesContext());
	}

	private FlowExecutionRepository getRepository(JsfExternalContext context) {
		return FlowFacesUtils.getExecutionRepository(context.getFacesContext());
	}
}
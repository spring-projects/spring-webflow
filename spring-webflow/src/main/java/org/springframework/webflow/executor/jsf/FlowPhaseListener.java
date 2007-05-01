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
import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.repository.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.execution.support.FlowExecutionRedirect;
import org.springframework.webflow.executor.RequestParameterInputMapper;
import org.springframework.webflow.executor.ResponseInstruction;
import org.springframework.webflow.executor.support.FlowExecutorArgumentHandler;
import org.springframework.webflow.executor.support.RequestParameterFlowExecutorArgumentHandler;
import org.springframework.webflow.executor.support.ResponseInstructionHandler;

/**
 * JSF phase listener responsible for managing the {@link FlowExecution} object lifecycle in a JSF environment. This
 * class handles restoring and saving a FlowExecution so other JSF artifacts that execute in different phases of the JSF
 * lifecycle may access conversational state and utilize Web Flow navigation behavior.
 * <p>
 * A restored flow execution is placed in a holder that other JSF artifacts such as VariableResolvers, PropertyResolvers
 * and NavigationHandlers may access during the request lifecycle. Once in the holder the execution is considered
 * "restored" and referred to as the "current" flow execution for this request.
 * <p>
 * <p>
 * This phase listener implements the following algorithm:
 * <ul>
 * <li>On BEFORE_RESTORE_VIEW, restore a {@link FlowExecution} if a call to
 * {@link FlowExecutorArgumentHandler#extractFlowExecutionKey(ExternalContext)} returns a valid flow execution key. This
 * occurs when a flow execution redirect or browser refresh is issued and ultimately results in a flow execution
 * refresh.
 * <li>On BEFORE_RESTORE_VIEW, launch a {@link FlowExecution} if a call to
 * {@link FlowExecutorArgumentHandler#extractFlowId(ExternalContext)} returns a valid flow id. This occurs when a
 * browser accesses a flow definition URL directly and is used to launch a new flow execution.
 * <li>During RESTORE_VIEW, the {@link FlowExecutionKeyStateHolder state holder component} will restore the current
 * FlowExecution if it is present in the JSF ViewRoot. This occurs when a postback from a JSF view that is participating
 * in a flow.
 * <li>On BEFORE_RENDER_RESPONSE, if a flow execution was restored in the RESTORE_VIEW phase generate a new key that
 * will identify the updated execution within the configured {@link FlowExecutionRepository}. Expose the new flow
 * execution key as a component in the view root for restoration on the next request.
 * <li>On AFTER_RENDER_RESPONSE, if a flow execution was restored in the RESTORE_VIEW phase <em>save</em> the updated
 * execution to the repository using the new key generated in the BEFORE_RENDER_RESPONSE phase.
 * </ul>
 * 
 * Note about customization: since PhaseListeners managed directly by the JSF provider cannot be benefit from
 * DependencyInjection, See Spring's {@link org.springframework.web.jsf.DelegatingPhaseListenerMulticaster} when you
 * need to customize a FlowPhaseListener instance.
 * 
 * @author Colin Sampaleanu
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public class FlowPhaseListener implements PhaseListener {

	/**
	 * Logger, usable by subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * A helper for handling arguments needed by this phase listener to restore and launch flow executions.
	 * 
	 * This helper is responsible for two main things:
	 * <ol>
	 * <li>Helping in the restoration of the "current" FlowExecution by extracting arguments from the request.
	 * Specifically:
	 * <ul>
	 * <li>The flowExecutionKey argument is extracted to perform a flow execution refresh on redirects and browser
	 * refreshes.
	 * <li>The flowId argument is extracted to perform a flow execution launch on direct browser access of a flow
	 * definition URL.
	 * </ul>
	 * <li>Generating URLs exposing the proper flow execution arguments. Specifically:
	 * <ul>
	 * <li>Generating the flow execution URL to redirect to on a FlowExecutionRedirect response.
	 * <li>Generating the flow definition URL to redirect to on a FlowDefinitionRedirect response.
	 * <li>Generating external URLs to redirect to on a ExternalRedirect repsonse.
	 * </ul>
	 * </ol>
	 * How arguments are extracted and how URLs are generated can be customized by setting a custom {{@link #setArgumentHandler(FlowExecutorArgumentHandler) argument handler}.
	 */
	private FlowExecutorArgumentHandler argumentHandler = new RequestParameterFlowExecutorArgumentHandler();

	/**
	 * The service responsible for mapping attributes of an {@link ExternalContext} to a new {@link FlowExecution}
	 * during the launch flow operation.
	 * <p>
	 * This allows developers to control what attributes are made available in the <code>inputMap</code> to new
	 * top-level flow executions. The starting execution may then choose to map that available input into its own local
	 * scope.
	 * <p>
	 * The default implementation simply exposes all request parameters as flow execution input attributes. May be null.
	 */
	private AttributeMapper inputMapper = new RequestParameterInputMapper();

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
	 * Sets the handler for arguments needed by this phase listener to restore and launch flow executions. This handler
	 * is responsible for two things:
	 * <ol>
	 * <li>Helping in the restoration of the "current" FlowExecution by extracting arguments from the request.
	 * Specifically:
	 * <ul>
	 * <li>The flowExecutionKey argument is extracted to perform a flow execution refresh on redirects and browser
	 * refreshes.
	 * <li>The flowId argument is extracted to perform a flow execution launch on direct browser access of a flow
	 * definition URL.
	 * </ul>
	 * <li>Generating URLs exposing the proper flow execution arguments. Specifically:
	 * <ul>
	 * <li>Generating the flow execution URL to redirect to on a FlowExecutionRedirect response.
	 * <li>Generating the flow definition URL to redirect to on a FlowDefinitionRedirect response.
	 * <li>Generating external URLs to redirect to on a ExternalRedirect response.
	 * </ul>
	 * </ol>
	 * @param argumentHandler the argument handler
	 */
	public void setArgumentHandler(FlowExecutorArgumentHandler argumentHandler) {
		this.argumentHandler = argumentHandler;
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
	 * @param inputMapper the input mapper
	 * @see RequestParameterInputMapper
	 */
	public void setInputMapper(AttributeMapper inputMapper) {
		this.inputMapper = inputMapper;
	}

	/**
	 * Returns the JSF view id resolver used by this phase listener.
	 */
	public ViewIdMapper getViewIdMapper() {
		return viewIdMapper;
	}

	/**
	 * Sets the JSF view id mapper used by this phase listener. The {@link ViewIdMapper} provides a mechanism to convert
	 * a logical Spring Web Flow application view name into a JSF view id.<br/>
	 * 
	 * JSF view ids are important to this phase listener: it uses them to check whether the current view has changed,
	 * and if a new view needs to be created and activated by delegating to the application's {@link ViewHandler}.<br/>
	 * 
	 * A view handler typically treats a JSF view id as the physical location of a view template encapsulating a page
	 * layout. The JSF view id normally specifies the physical location of the view template minus a suffix. View
	 * handlers typically replace the suffix of any view id with their own default suffix (e.g. ".jsp" or ".xhtml") and
	 * then try to locate a physical template view.<br/>
	 * 
	 * The {@link ViewIdMapper} provides the ability to customize how SWF view name is mapped to a JSF view id that will
	 * be passed to the ViewHandler. The default value for the view id mapper is a {@link DefaultViewIdMapper} which
	 * just returns the SWF viewId as-is.
	 * @param viewIdMapper the view id mapper
	 * @see #prepareApplicationView(FacesContext, FlowExecutionHolder)
	 */
	public void setViewIdMapper(ViewIdMapper viewIdMapper) {
		this.viewIdMapper = viewIdMapper;
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	public void beforePhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
			try {
				restoreFlowExecution(event.getFacesContext());
			}
			catch (RuntimeException e) {
				// clear the current external context only - no lock is acquired at this point
				ExternalContextHolder.setExternalContext(null);
				throw e;
			}
			catch (Error e) {
				ExternalContextHolder.setExternalContext(null);
				throw e;
			}
		}
		else if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			if (FlowExecutionHolderUtils.isFlowExecutionRestored(event.getFacesContext())) {
				try {
					prepareResponse(getCurrentContext(), FlowExecutionHolderUtils.getFlowExecutionHolder(context));
				}
				catch (RuntimeException e) {
					cleanupResources(context);
					throw e;
				}
				catch (Error e) {
					cleanupResources(context);
					throw e;
				}
			}
		}
	}

	public void afterPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			if (FlowExecutionHolderUtils.isFlowExecutionRestored(context)) {
				try {
					saveFlowExecution(getCurrentContext(), FlowExecutionHolderUtils.getFlowExecutionHolder(context));
				}
				finally {
					// always cleanup after save - done with flow execution request processing
					cleanupResources(context);
				}
			}
		}
		else {
			// cleanup if some other JSF artifact marked 'response complete' to short-circuit the lifecycle early
			if (context.getResponseComplete()) {
				cleanupResources(context);
			}
		}
	}

	protected void restoreFlowExecution(FacesContext facesContext) {
		JsfExternalContext context = new JsfExternalContext(facesContext);
		if (argumentHandler.isFlowExecutionKeyPresent(context)) {
			// restore flow execution from repository so it will be available togother JSF artifacts
			// (this could happen as part of a flow execution redirect or browser refresh)
			FlowExecutionRepository repository = getRepository(context);
			FlowExecutionKey flowExecutionKey = repository.parseFlowExecutionKey(argumentHandler.extractFlowExecutionKey(context));
			FlowExecutionLock lock = repository.getLock(flowExecutionKey);
			lock.lock();
			FlowExecution flowExecution;
			try {
				flowExecution = repository.getFlowExecution(flowExecutionKey);
				if (logger.isDebugEnabled()) {
					logger.debug("Loaded existing flow execution key '" + flowExecutionKey + "' due to browser access "
							+ "[either via a flow execution redirect or direct browser refresh]");
				}
			}
			catch (RuntimeException e) {
				lock.unlock();
				throw e;
			}
			catch (Error e) {
				lock.unlock();
				throw e;
			}
			FlowExecutionHolderUtils.setFlowExecutionHolder(new FlowExecutionHolder(flowExecutionKey, flowExecution, lock), facesContext);
		}
		else if (argumentHandler.isFlowIdPresent(context)) {
			// launch a new flow execution
			// (this could happen as part of direct browser access or a flow definition redirect)
			String flowId = argumentHandler.extractFlowId(context);
			FlowDefinition flowDefinition = getLocator(context).getFlowDefinition(flowId);
			FlowExecution flowExecution = getFactory(context).createFlowExecution(flowDefinition);
			FlowExecutionHolder holder = new FlowExecutionHolder(flowExecution);
			FlowExecutionHolderUtils.setFlowExecutionHolder(holder, facesContext);
			ViewSelection selectedView = flowExecution.start(createInput(context), context);
			holder.setViewSelection(selectedView);
			if (logger.isDebugEnabled()) {
				logger.debug("Launched a new flow execution due to browser access "
						+ "[either via a flow redirect or direct browser URL access]");
			}
		}
	}

	/**
	 * Factory method that creates the input attribute map for a newly created {@link FlowExecution}. This
	 * implementation uses the registered input mapper, if any.
	 * 
	 * @param context the external context
	 * @return the input map, or null if no input
	 */
	protected MutableAttributeMap createInput(ExternalContext context) {
		if (inputMapper != null) {
			MutableAttributeMap inputMap = new LocalAttributeMap();
			inputMapper.map(context, inputMap, null);
			return inputMap;
		}
		else {
			return null;
		}
	}

	/**
	 * Prepare the appropriate JSF response (e.g. rendering a view, sending a redirect, etc).
	 * 
	 * @param context the context
	 * @param holder the holder
	 */
	protected void prepareResponse(final JsfExternalContext context, final FlowExecutionHolder holder) {
		ViewSelection selectedView = holder.getViewSelection();
		if (selectedView == null) {
			// no navigation event has been processed - simply refresh the execution with the same key
			selectedView = holder.getFlowExecution().refresh(context);
			holder.setViewSelection(selectedView);
		}
		else {
			// an navigation event has been processed - generate a new flow execution key if necessary
			generateKey(context, holder);
		}
		new ResponseInstructionHandler() {
			protected void handleApplicationView(ApplicationView view) throws Exception {
				prepareApplicationView(context.getFacesContext(), holder);
			}

			protected void handleFlowDefinitionRedirect(FlowDefinitionRedirect redirect) throws Exception {
				String url = argumentHandler.createFlowDefinitionUrl(redirect, context);
				sendRedirect(url, context);
			}

			protected void handleFlowExecutionRedirect(FlowExecutionRedirect redirect) throws Exception {
				String url = argumentHandler.createFlowExecutionUrl(holder.getFlowExecutionKey().toString(), holder
						.getFlowExecution(), context);
				sendRedirect(url, context);
			}

			protected void handleExternalRedirect(ExternalRedirect redirect) throws Exception {
				String flowExecutionKey = holder.getFlowExecution().isActive() ? holder.getFlowExecutionKey()
						.toString() : null;
				String url = argumentHandler.createExternalUrl(redirect, flowExecutionKey, context);
				sendRedirect(url, context);
			}

			protected void handleNull() throws Exception {
				// nothing to do
			}

		}.handleQuietly(new ResponseInstruction(holder.getFlowExecution(), selectedView));
	}

	/**
	 * Prepare the JSF view for rendering.
	 * 
	 * @param facesContext the faces context
	 * @param holder the holder of the current flow execution
	 */
	protected void prepareApplicationView(FacesContext facesContext, FlowExecutionHolder holder) {
		ApplicationView view = (ApplicationView) holder.getViewSelection();
		if (view != null) {
			// expose the view's "model map" in the request map
			putInto(facesContext.getExternalContext().getRequestMap(), view.getModel());
			// update the root component if necessary
			updateViewRoot(facesContext, viewIdMapper.mapViewId(view.getViewName()));
		}
		String flowExecutionKey = holder.getFlowExecution().isActive() ? holder.getFlowExecutionKey().toString() : null;
		if (flowExecutionKey != null) {
			saveInViewRoot(facesContext, flowExecutionKey);
		}
		Map requestMap = facesContext.getExternalContext().getRequestMap();
		argumentHandler.exposeFlowExecutionContext(flowExecutionKey, holder.getFlowExecution(), requestMap);
	}

	/**
	 * Updates the current flow execution in the repository.
	 * 
	 * @param context the external context
	 * @param holder the current flow execution holder
	 */
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

	// private helpers

	private JsfExternalContext getCurrentContext() {
		return (JsfExternalContext) ExternalContextHolder.getExternalContext();
	}

	private void cleanupResources(FacesContext context) {
		FlowExecutionHolderUtils.unlockCurrentFlowExecutionIfNecessary(context);
		ExternalContextHolder.setExternalContext(null);
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

	/**
	 * Saves the flow execution key in a component in the view root for restoration on subsequent RESTORE_VIEW
	 * operations.
	 * 
	 * @param facesContext the faces context exposing the view root
	 * @param flowExecutionKey the flow execution key
	 */
	private void saveInViewRoot(FacesContext facesContext, String flowExecutionKey) {
		// search for key holder in the component tree
		FlowExecutionKeyStateHolder keyHolder = (FlowExecutionKeyStateHolder) facesContext.getViewRoot().findComponent(
				FlowExecutionKeyStateHolder.COMPONENT_ID);
		if (keyHolder == null) {
			keyHolder = new FlowExecutionKeyStateHolder();
			// expose in the view root for preservation in the component tree
			facesContext.getViewRoot().getChildren().add(keyHolder);
		}
		keyHolder.setFlowExecutionKey(flowExecutionKey);
	}

	private void generateKey(JsfExternalContext context, FlowExecutionHolder holder) {
		FlowExecution flowExecution = holder.getFlowExecution();
		if (flowExecution.isActive()) {
			// generate new continuation key for the flow execution before rendering the response
			FlowExecutionKey flowExecutionKey = holder.getFlowExecutionKey();
			FlowExecutionRepository repository = getRepository(context);
			if (flowExecutionKey == null) {
				// it is a new conversation - generate a brand new key
				flowExecutionKey = repository.generateKey(flowExecution);
				FlowExecutionLock lock = repository.getLock(flowExecutionKey);
				lock.lock();
				// set that the flow execution lock has been acquired
				holder.setFlowExecutionLock(lock);
			}
			else {
				// it is an existing conversation - get the next key
				flowExecutionKey = repository.getNextKey(flowExecution, flowExecutionKey);
			}
			holder.setFlowExecutionKey(flowExecutionKey);
		}
	}

	/**
	 * Utility method needed needed only because we can not rely on JSF RequestMap supporting Map's putAll method. Tries
	 * putAll, falls back to individual adds
	 * 
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
			url = context.getFacesContext().getExternalContext().encodeResourceURL(url);
			context.getFacesContext().getExternalContext().redirect(url);
			context.getFacesContext().responseComplete();
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not send redirect to " + url);
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

	/**
	 * Standard default view id resolver which uses the web flow view name as the jsf view id
	 */
	public static class DefaultViewIdMapper implements ViewIdMapper {
		public String mapViewId(String viewName) {
			return viewName;
		}
	}
}
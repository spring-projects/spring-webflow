package org.springframework.webflow.context.portlet;

import java.io.IOException;
import java.io.Writer;
import java.security.Principal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.LocalParameterMap;
import org.springframework.webflow.core.collection.LocalSharedAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;

/**
 * Provides contextual information about an portlet environment that has interacted with Spring Web Flow.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Jeremy Grelle
 * @author Scott Andrews
 */
public class PortletExternalContext implements ExternalContext {

	protected static final short ACTION_PHASE = 1;
	protected static final short RENDER_PHASE = 2;

	/**
	 * The context.
	 */
	private PortletContext context;

	/**
	 * The request.
	 */
	private PortletRequest request;

	/**
	 * The response.
	 */
	private PortletResponse response;

	/**
	 * The portlet request phase: render or action
	 */
	private short requestPhase;

	/**
	 * An accessor for the HTTP request parameter map.
	 */
	private ParameterMap requestParameterMap;

	/**
	 * An accessor for the HTTP request attribute map.
	 */
	private MutableAttributeMap requestMap;

	/**
	 * An accessor for the HTTP session map.
	 */
	private SharedAttributeMap sessionMap;

	/**
	 * An accessor for the servlet context application map.
	 */
	private SharedAttributeMap applicationMap;

	/**
	 * A flag indicating if a flow execution redirect has been requested.
	 */
	private boolean flowExecutionRedirectRequested;

	/**
	 * A string specifying the id of the flow to redirect to after request processing. If null, no flow definition
	 * redirect has been requested.
	 */
	private String flowDefinitionRedirectFlowId;

	/**
	 * Input to pass the flow definition upon redirecting. May be null. Never set unless
	 * {@link #flowDefinitionRedirectFlowId} has been set.
	 */
	private AttributeMap flowDefinitionRedirectFlowInput;

	/**
	 * A string specifying an arbitrary
	 */
	private String externalRedirectUrl;

	/**
	 * The strategy for generating flow execution urls.
	 */
	private FlowUrlHandler flowUrlHandler;

	/**
	 * Whether this external request context originated from an Ajax request or not.
	 */
	private boolean ajaxRequest;

	/**
	 * In the case where a redirect response is requested, this flag indicates if the redirect should be issued from a
	 * popup dialog.
	 */
	private boolean redirectInPopup;

	/**
	 * Create a new external context wrapping given portlet action request and response and given portlet context.
	 * @param context the portal context
	 * @param request the portlet request
	 * @param response the portlet response
	 */
	public PortletExternalContext(PortletContext context, PortletRequest request, PortletResponse response) {
		init(context, request, response, new DefaultFlowUrlHandler());
	}

	/**
	 * Create a new external context wrapping given portlet action request and response and given portlet context.
	 * @param context the portal context
	 * @param request the portlet request
	 * @param response the portlet response
	 * @param flowUrlHandler the flow url handler
	 */
	public PortletExternalContext(PortletContext context, PortletRequest request, PortletResponse response,
			FlowUrlHandler flowUrlHandler) {
		init(context, request, response, flowUrlHandler);
	}

	/**
	 * Indicates if the current request from this client is an ajax request. This flag may effect the handling of
	 * response writing within Spring Web Flow.
	 * @param ajaxRequest the ajax request flag
	 */
	public void setAjaxRequest(boolean ajaxRequest) {
		this.ajaxRequest = ajaxRequest;
	}

	// implementing external context

	public String getContextPath() {
		return request.getContextPath();
	}

	public ParameterMap getRequestParameterMap() {
		return requestParameterMap;
	}

	public MutableAttributeMap getRequestMap() {
		return requestMap;
	}

	public SharedAttributeMap getSessionMap() {
		return sessionMap;
	}

	public SharedAttributeMap getGlobalSessionMap() {
		return getSessionMap();
	}

	public SharedAttributeMap getApplicationMap() {
		return applicationMap;
	}

	public Principal getCurrentUser() {
		return request.getUserPrincipal();
	}

	public Object getNativeContext() {
		return context;
	}

	public Object getNativeRequest() {
		return request;
	}

	public Object getNativeResponse() {
		return response;
	}

	public boolean isAjaxRequest() {
		return ajaxRequest;
	}

	public String getFlowExecutionUri(String flowId, String flowExecutionKey) {
		if (this.isRenderPhase()) {
			return flowUrlHandler.createFlowExecutionUrl(flowId, flowExecutionKey, (RenderResponse) response);
		} else {
			throw new IllegalStateException("Only a render request can obtain an flow execution uri");
		}
	}

	public Writer getResponseWriter() {
		try {
			if (isRenderPhase()) {
				return ((RenderResponse) response).getWriter();
			} else {
				throw new IllegalStateException("Only render requests can obtain response writer");
			}
		} catch (IOException e) {
			throw new IllegalStateException("Unable to obtain response writer", e);
		}
	}

	public boolean isResponseCommitted() {
		return flowExecutionRedirectRequested() || flowDefinitionRedirectRequested() || externalRedirectRequested();
	}

	public boolean isResponseAllowed() {
		return isRenderPhase();
	}

	public void requestFlowExecutionRedirect() {
		flowExecutionRedirectRequested = true;
	}

	public void requestExternalRedirect(String uri) {
		externalRedirectUrl = uri;
	}

	public void requestFlowDefinitionRedirect(String flowId, AttributeMap input) {
		flowDefinitionRedirectFlowId = flowId;
		flowDefinitionRedirectFlowInput = input;
	}

	public void requestRedirectInPopup() {
		redirectInPopup = true;
	}

	// implementation specific methods

	/**
	 * Returns the flag indicating if a flow execution redirect response has been requested by the flow.
	 */
	public boolean flowExecutionRedirectRequested() {
		return flowExecutionRedirectRequested;
	}

	/**
	 * Returns the flag indicating if a flow definition redirect response has been requested by the flow.
	 */
	public boolean flowDefinitionRedirectRequested() {
		return flowDefinitionRedirectFlowId != null;
	}

	/**
	 * Returns the id of the flow definition to redirect to. Only set when {@link #flowDefinitionRedirectRequested()}
	 * returns true.
	 */
	public String getFlowRedirectFlowId() {
		return flowDefinitionRedirectFlowId;
	}

	/**
	 * Returns the input to pass the flow definition through the redirect. Only set when
	 * {@link #flowDefinitionRedirectRequested()} returns true.
	 */
	public AttributeMap getFlowRedirectFlowInput() {
		return flowDefinitionRedirectFlowInput;
	}

	/**
	 * Returns the flag indicating if an external redirect response has been requested by the flow.
	 */
	public boolean externalRedirectRequested() {
		return externalRedirectUrl != null;
	}

	/**
	 * Returns the URL to redirect to. Only set if {@link #externalRedirectRequested()} returns true.
	 */
	public String getExternalRedirectUrl() {
		return externalRedirectUrl;
	}

	/**
	 * If a redirect response has been requested, indicates if the redirect should be issued from a popup dialog.
	 */
	public boolean redirectInPopup() {
		return redirectInPopup;
	}

	/**
	 * Returns true if the current request phase is the action phase
	 */
	public boolean isActionPhase() {
		return requestPhase == ACTION_PHASE;
	}

	/**
	 * Returns true if the current request phase is the render phase
	 */
	public boolean isRenderPhase() {
		return requestPhase == RENDER_PHASE;
	}

	// private helpers

	private void init(PortletContext context, PortletRequest request, PortletResponse response,
			FlowUrlHandler flowUrlHandler) {
		this.context = context;
		this.request = request;
		this.response = response;
		this.requestParameterMap = new LocalParameterMap(new PortletRequestParameterMap(request));
		this.requestMap = new LocalAttributeMap(new PortletRequestMap(request));
		this.sessionMap = new LocalSharedAttributeMap(new PortletSessionMap(request));
		this.applicationMap = new LocalSharedAttributeMap(new PortletContextMap(context));
		this.flowUrlHandler = flowUrlHandler;
		if (request instanceof ActionRequest && response instanceof ActionResponse) {
			requestPhase = ACTION_PHASE;
		} else if (request instanceof RenderRequest && response instanceof RenderResponse) {
			requestPhase = RENDER_PHASE;
		} else {
			throw new IllegalArgumentException("Unknown portlet phase, expected: action or render");
		}
	}

}

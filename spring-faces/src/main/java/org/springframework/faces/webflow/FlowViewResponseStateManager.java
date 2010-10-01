/*
 * Copyright 2004-2010 the original author or authors.
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
package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.Writer;

import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * <p>
 * A custom ResponseStateManager that writes JSF state to a Web Flow managed view-scoped variable. This class is plugged
 * in via {@link FlowRenderKit} in JSF 2 runtime environments only.
 * </p>
 * 
 * <p>
 * In JSF 2 where a partial state saving algorithm is used, Web Flow delegates to the JSF 2 runtime to handle state
 * saving. However, an instance of this class plugged in via {@link FlowRenderKit} will ensure that state is saved in a
 * Web Flow managed view-scoped variable.
 * </p>
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class FlowViewResponseStateManager extends ResponseStateManager {

	private static final Log logger = LogFactory.getLog(FlowViewResponseStateManager.class);

	private ResponseStateManager delegate;

	private char[] stateFieldStart = ("<input type=\"hidden\" name=\"" + ResponseStateManager.VIEW_STATE_PARAM
			+ "\" id=\"" + ResponseStateManager.VIEW_STATE_PARAM + "\" value=\"").toCharArray();

	private char[] stateFieldEnd = "\" />".toCharArray();

	public FlowViewResponseStateManager(ResponseStateManager delegate) {
		this.delegate = delegate;
	}

	/**
	 * <p>
	 * Wraps state in an instance of {@link FlowSerializedView} and stores it in view scope.
	 * </p>
	 * 
	 * <p>
	 * Also complies with the contract for {@link ResponseStateManager#writeState(FacesContext, Object)} by writing the
	 * "javax.faces.ViewState" and optionally the "javax.faces.RenderKitId" hidden input fields to the response.
	 * </p>
	 */
	@Override
	public void writeState(FacesContext facesContext, Object state) throws IOException {
		if (!JsfUtils.isFlowRequest()) {
			delegate.writeState(facesContext, state);
		} else {
			FlowSerializedView view = null;
			if (state instanceof FlowSerializedView) {
				view = (FlowSerializedView) state;
			} else {
				Object[] serializedState = (Object[]) state;
				view = new FlowSerializedView(facesContext.getViewRoot().getViewId(), serializedState[0],
						serializedState[1]);
			}
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			requestContext.getViewScope().put(FlowViewStateManager.SERIALIZED_VIEW_STATE, view);

			ResponseWriter writer = facesContext.getResponseWriter();
			writeViewStateField(facesContext, writer);
			writeRenderKitIdField(facesContext, writer);
		}
	}

	/**
	 * <p>
	 * Retrieves the state from view scope as an instance of {@link FlowSerializedView} and turns it to an array before
	 * returning.
	 * </p>
	 */
	@Override
	public Object getState(FacesContext facesContext, String viewId) {
		if (!JsfUtils.isFlowRequest()) {
			return delegate.getState(facesContext, viewId);
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		FlowSerializedView view = (FlowSerializedView) requestContext.getViewScope().get(
				FlowViewStateManager.SERIALIZED_VIEW_STATE);
		Object[] state = null;
		if (view == null) {
			logger.debug("No matching view in view scope");
		} else {
			state = new Object[] { view.getTreeStructure(), view.getComponentState() };
		}
		return state;
	}

	/**
	 * This method returns the flow execution key to be used as the value for the "javax.faces.ViewState" hidden input
	 * field. The value of this key is not important because JSF state is stored in a Web Flow managed view scoped
	 * variable. However the presence of the view state parameter alone is important for triggering actions. Hence we
	 * return the most logical value, which is the flow execution key.
	 */
	@Override
	public String getViewState(FacesContext facesContext, Object state) {
		if (!JsfUtils.isFlowRequest()) {
			return delegate.getViewState(facesContext, state);
		}
		return getFlowExecutionKey();
	}

	// ------------------- Delegation methods ------------------//

	@Override
	public boolean isPostback(FacesContext context) {
		return delegate.isPostback(context);
	}

	@Override
	public Object getTreeStructureToRestore(FacesContext context, String viewId) {
		return delegate.getTreeStructureToRestore(context, viewId);
	}

	@Override
	public Object getComponentStateToRestore(FacesContext context) {
		return delegate.getComponentStateToRestore(context);
	}

	@Override
	public void writeState(FacesContext context, SerializedView state) throws IOException {
		delegate.writeState(context, state);
	}

	// ------------------- Private helper methods ------------------//

	private String getFlowExecutionKey() {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		return requestContext.getFlowExecutionContext().getKey().toString();
	}

	/**
	 * See comments on {@link ResponseStateManager#VIEW_STATE_PARAM}.
	 * 
	 * @param context the <code>FacesContext</code> for the current request
	 * @param writer the <code>ResponseWriter</code> to write to
	 * @throws IOException if an error occurs writing to the client
	 */
	private void writeViewStateField(FacesContext context, Writer writer) throws IOException {
		writer.write(stateFieldStart);
		writer.write(getFlowExecutionKey());
		writer.write(stateFieldEnd);
	}

	/**
	 * See comments on <code>ResponseStateManager.RENDER_KIT_ID_PARAM</code> in
	 * {@link ResponseStateManager#writeState(FacesContext, Object)}.
	 * 
	 * @param context the <code>FacesContext</code> for the current request
	 * @param writer the <code>ResponseWriter</code> to write to
	 * @throws IOException if an error occurs writing to the client
	 */
	private void writeRenderKitIdField(FacesContext context, ResponseWriter writer) throws IOException {
		String result = context.getApplication().getDefaultRenderKitId();
		if (result != null && !RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(result)) {
			writer.startElement("input", context.getViewRoot());
			writer.writeAttribute("type", "hidden", "type");
			writer.writeAttribute("name", ResponseStateManager.RENDER_KIT_ID_PARAM, "name");
			writer.writeAttribute("value", result, "value");
			writer.endElement("input");
		}
	}

}

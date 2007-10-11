package org.springframework.faces.webflow;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.springframework.webflow.execution.RequestContextHolder;

public class FlowExecutionViewHandler extends ViewHandler {

	ViewHandler delegate;

	public FlowExecutionViewHandler(ViewHandler delegate) {
		this.delegate = delegate;
	}

	public String getActionURL(FacesContext context, String viewId) {
		return RequestContextHolder.getRequestContext().getFlowExecutionUrl();
	}

	// ------------------- Pass-through delegate methods ------------------//
	public String calculateCharacterEncoding(FacesContext context) {
		return delegate.calculateCharacterEncoding(context);
	}

	public Locale calculateLocale(FacesContext context) {
		return delegate.calculateLocale(context);
	}

	public String calculateRenderKitId(FacesContext context) {
		return delegate.calculateRenderKitId(context);
	}

	public UIViewRoot createView(FacesContext context, String viewId) {
		return delegate.createView(context, viewId);
	}

	public String getResourceURL(FacesContext context, String path) {
		return delegate.getResourceURL(context, path);
	}

	public void initView(FacesContext context) throws FacesException {
		delegate.initView(context);
	}

	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		delegate.renderView(context, viewToRender);
	}

	public UIViewRoot restoreView(FacesContext context, String viewId) {
		return delegate.restoreView(context, viewId);
	}

	public void writeState(FacesContext context) throws IOException {
		delegate.writeState(context);
	}

}

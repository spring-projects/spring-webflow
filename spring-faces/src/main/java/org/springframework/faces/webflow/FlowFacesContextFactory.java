package org.springframework.faces.webflow;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

public class FlowFacesContextFactory extends FacesContextFactory {

	FacesContextFactory delegate;

	public FlowFacesContextFactory(FacesContextFactory delegate) {
		this.delegate = delegate;
	}

	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {
		return new FlowFacesContext(delegate.getFacesContext(context, request, response, lifecycle));
	}

}

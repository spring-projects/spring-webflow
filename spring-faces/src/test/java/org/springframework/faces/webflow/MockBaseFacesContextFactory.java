package org.springframework.faces.webflow;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shale.test.mock.MockExternalContext;

public class MockBaseFacesContextFactory extends FacesContextFactory {

	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {

		ExternalContext ext = new MockExternalContext((ServletContext) context, (HttpServletRequest) request,
				(HttpServletResponse) response);

		return new MockBaseFacesContext(ext, lifecycle);
	}

}

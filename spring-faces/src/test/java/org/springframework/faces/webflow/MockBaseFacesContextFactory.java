package org.springframework.faces.webflow;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.test.mock.MockExternalContext20;
import org.apache.myfaces.test.mock.MockHttpServletResponse;

public class MockBaseFacesContextFactory extends FacesContextFactory {

	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {

		if (FacesContext.getCurrentInstance() != null
				&& FacesContext.getCurrentInstance() instanceof MockBaseFacesContext) {
			return FacesContext.getCurrentInstance();
		} else {

			ExternalContext ext = new MockExternalContext20((ServletContext) context, (HttpServletRequest) request,
					(HttpServletResponse) response) {

				public void setResponseBufferSize(int size) {
					((MockHttpServletResponse) getResponse()).setBufferSize(size);
				}

				public void setResponseContentLength(int length) {
					((MockHttpServletResponse) getResponse()).setContentLength(length);
				}

				public void setResponseContentType(String contentType) {
					((MockHttpServletResponse) getResponse()).setContentType(contentType);
				}

				public void setResponseStatus(int statusCode) {
					((MockHttpServletResponse) getResponse()).setStatus(statusCode);
				}

			};

			return new MockBaseFacesContext(ext, lifecycle);
		}
	}

}

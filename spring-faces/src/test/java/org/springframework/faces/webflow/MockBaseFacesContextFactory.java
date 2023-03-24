package org.springframework.faces.webflow;

import jakarta.faces.FacesException;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextFactory;
import jakarta.faces.lifecycle.Lifecycle;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.myfaces.test.mock.MockExternalContext;
import org.apache.myfaces.test.mock.MockHttpServletResponse;

public class MockBaseFacesContextFactory extends FacesContextFactory {

	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {

		if (FacesContext.getCurrentInstance() != null
				&& FacesContext.getCurrentInstance() instanceof MockBaseFacesContext) {
			return FacesContext.getCurrentInstance();
		} else {

			ExternalContext ext = new MockExternalContext((ServletContext) context, (HttpServletRequest) request,
					(HttpServletResponse) response) {

				public void setResponseBufferSize(int size) {
					((MockHttpServletResponse) getResponse()).setBufferSize(size);
				}

				public void setResponseContentLength(int length) {
					((MockHttpServletResponse) getResponse()).setContentLength(length);
				}

				@Override
				public void release() {
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

package org.springframework.faces.webflow;

import java.io.IOException;
import java.net.URL;

import javax.faces.FacesException;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;

public class FlowResourceResolver implements ResourceResolver {

	ResourceResolver delegateResolver = new DefaultResourceResolver();

	public URL resolveUrl(String path) {

		if (!JsfUtils.isFlowRequest()) {
			return delegateResolver.resolveUrl(path);
		}

		try {
			RequestContext context = RequestContextHolder.getRequestContext();
			ApplicationContext flowContext = context.getActiveFlow().getApplicationContext();
			if (flowContext == null) {
				throw new IllegalStateException("A Flow ApplicationContext is required to resolve Flow View Resources");
			}

			ApplicationContext appContext = flowContext.getParent();
			Resource viewResource = appContext.getResource(path);
			if (viewResource.exists()) {
				return viewResource.getURL();
			} else {
				return delegateResolver.resolveUrl(path);
			}
		} catch (IOException ex) {
			throw new FacesException(ex);
		}
	}

}

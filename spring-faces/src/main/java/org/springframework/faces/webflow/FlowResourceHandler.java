/*
 * Copyright 2004-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.webflow;

import java.io.IOException;
import java.net.URL;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceHandler;
import jakarta.faces.application.ResourceHandlerWrapper;
import jakarta.faces.application.ViewResource;
import jakarta.faces.context.FacesContext;

/**
 * Resolves Facelets resources using Spring Resource paths such as "classpath:foo.xhtml".
 * <p>This handler is auto-configured in the faces-config.xml bundled with the
 * "spring-faces" module.
 * @since 2.5
 */
public class FlowResourceHandler extends ResourceHandlerWrapper {

	private final ResourceHandler wrapped;

	public FlowResourceHandler(ResourceHandler delegate) {
		this.wrapped = delegate;
	}

	@Override
	public ResourceHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public ViewResource createViewResource(FacesContext facesContext, String resourceName) {
		if (!JsfUtils.isFlowRequest()) {
			return this.wrapped.createViewResource(facesContext, resourceName);
		}

		try {
			RequestContext context = RequestContextHolder.getRequestContext();
			ApplicationContext flowContext = context.getActiveFlow().getApplicationContext();
			if (flowContext == null) {
				throw new IllegalStateException(
						"A Flow ApplicationContext is required to resolve Flow View Resources");
			}
			ApplicationContext appContext = flowContext.getParent();
			Resource viewResource = appContext.getResource(resourceName);
			URL url = viewResource.getURL();
			if (viewResource.exists()) {
				return new ViewResource() {
					@Override
					public URL getURL() {
						return url;
					}
				};
			} else {
				return this.wrapped.createViewResource(facesContext, resourceName);
			}
		} catch (IOException ex) {
			throw new FacesException(ex);
		}
	}

}

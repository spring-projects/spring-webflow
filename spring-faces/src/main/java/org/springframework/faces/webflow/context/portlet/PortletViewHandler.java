/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.faces.webflow.context.portlet;

import java.util.List;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.portlet.MimeResponse;
import javax.portlet.ResourceURL;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * JSF {@link ViewHandler} that adds support for generating Portlet compatible resource URLs.
 * 
 * @since 2.4.0
 * @author Phillip Webb
 */
public class PortletViewHandler extends ViewHandlerWrapper {

	private static final String FACES_RESOURCE = "javax.faces.resource";

	private ViewHandler wrapped;

	public PortletViewHandler(ViewHandler wrapped) {
		this.wrapped = wrapped;
	}

	public ViewHandler getWrapped() {
		return this.wrapped;
	}

	public String getResourceURL(FacesContext context, String path) {
		String uri = super.getResourceURL(context, path);
		int facesResourceIndex = (uri == null ? -1 : uri.indexOf(FACES_RESOURCE));
		if (facesResourceIndex == -1) {
			return uri;
		}
		UriComponents components = UriComponentsBuilder.fromUriString(uri.substring(facesResourceIndex + FACES_RESOURCE.length() + 1)).build();
		MimeResponse response = (MimeResponse) context.getExternalContext().getResponse();
		ResourceURL resourceURL = response.createResourceURL();
		for (Map.Entry<String, List<String>> entry : components.getQueryParams().entrySet()) {
			String name = entry.getKey();
			List<String> value = entry.getValue();
			resourceURL.setParameter(name, value.toArray(new String[value.size()]));
		}
		resourceURL.setParameter(FACES_RESOURCE, components.getPath());
		return resourceURL.toString();
	}

}

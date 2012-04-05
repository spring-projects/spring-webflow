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
package org.springframework.faces.webflow;

import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;

/**
 * Extends FlowViewHandler in order to provide JSF 2 delegation method. This is necessary because some of the methods
 * use JSF 2 specific types as input or output parameters.
 * 
 * @author Rossen Stoyanchev
 */
public class Jsf2FlowViewHandler extends FlowViewHandler {

	public Jsf2FlowViewHandler(ViewHandler delegate) {
		super(delegate);
	}

	// --------------- JSF 2.0 Pass-through delegate methods ------------------//

	public String calculateCharacterEncoding(FacesContext context) {
		return getDelegate().calculateCharacterEncoding(context);
	}

	public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		return getDelegate().getBookmarkableURL(context, viewId, parameters, includeViewParams);
	}

	public String getRedirectURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		return getDelegate().getRedirectURL(context, viewId, parameters, includeViewParams);
	}

	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		return getDelegate().getViewDeclarationLanguage(context, viewId);
	}

	public void initView(FacesContext context) throws FacesException {
		getDelegate().initView(context);
	}

}

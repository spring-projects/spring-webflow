/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.executor.struts;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.springframework.webflow.context.servlet.ServletExternalContext;

/**
 * Provides consistent access to a Struts environment from within the Spring Web
 * Flow system. Represents the context of a request into SWF from Struts.
 * 
 * @author Keith Donald
 */
public class StrutsExternalContext extends ServletExternalContext {

	/**
	 * The Struts action mapping associated with this request.
	 */
	private ActionMapping actionMapping;

	/**
	 * The Struts action form associated with this request.
	 */
	private ActionForm actionForm;

	/**
	 * Creates a new Struts external context.
	 * @param mapping the action mapping
	 * @param form the action form
	 * @param context the servlet context
	 * @param request the request
	 * @param response the response
	 */
	public StrutsExternalContext(ActionMapping mapping, ActionForm form, ServletContext context,
			HttpServletRequest request, HttpServletResponse response) {
		super(context, request, response);
		this.actionMapping = mapping;
		this.actionForm = form;
	}

	/**
	 * Returns the action form.
	 */
	public ActionForm getActionForm() {
		return actionForm;
	}

	/**
	 * Returns the action mapping.
	 */
	public ActionMapping getActionMapping() {
		return actionMapping;
	}
}
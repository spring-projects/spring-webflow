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

package org.springframework.faces.webflow.application.portlet;

import javax.faces.application.ViewHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.faces.webflow.context.portlet.PortletViewHandler;

/**
 * {@link ViewHandler} implementation for Portlets. This class is provided for compatibility with Web Flow v2.2.0, users
 * should replace references in their <tt>faces-confix.xml</tt> with
 * {@link org.springframework.faces.webflow.context.portlet.PortletViewHandler}.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.2.0
 * @deprecated In favor of org.springframework.faces.webflow.context.portlet.PortletViewHandler 
 */
@Deprecated
public class PortletFaceletViewHandler extends PortletViewHandler {

	private Log logger = LogFactory.getLog(getClass());

	public PortletFaceletViewHandler(ViewHandler wrapped) {
		super(wrapped);
		this.logger.warn("*****");
		this.logger.warn("***** PLEASE UPDATE YOUR faces-confix.xml");
		this.logger.warn("*****");
		this.logger.warn("***** org.springframework.faces.webflow.application.portlet.PortletFaceletViewHandler has been deprecated");
		this.logger.warn("***** please update references to use org.springframework.faces.webflow.context.portlet.PortletViewHandler");
		this.logger.warn("*****");
	}
}

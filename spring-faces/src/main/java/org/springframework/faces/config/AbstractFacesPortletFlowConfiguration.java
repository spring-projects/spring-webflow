/*
 * Copyright 2004-2014 the original author or authors.
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
package org.springframework.faces.config;

import org.springframework.context.annotation.Bean;
import org.springframework.faces.webflow.context.portlet.JsfResourceRequestHandler;

/**
 * Extends {@link AbstractFacesFlowConfiguration} and registers a
 * {@link JsfResourceRequestHandler} bean for serving resources in a Portlet environment.

 * @author Rossen Stoyanchev
 * @since 2.4
 */
public class AbstractFacesPortletFlowConfiguration extends AbstractFacesFlowConfiguration {

	@Bean
	public JsfResourceRequestHandler jsfPortletResourceRequestHandler() {
		return new JsfResourceRequestHandler();
	}

}

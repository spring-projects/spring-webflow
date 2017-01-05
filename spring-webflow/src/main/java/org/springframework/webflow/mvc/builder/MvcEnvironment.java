/*
 * Copyright 2004-2008 the original author or authors.
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
package org.springframework.webflow.mvc.builder;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * Supported Spring Web MVC environments.
 * 
 * @author Keith Donald
 */
public enum MvcEnvironment {

	/**
	 * Spring Web Servlet MVC.
	 */
	SERVLET,

	/**
	 * Spring Web Portlet MVC.
	 */
	PORTLET;

	/**
	 * Calculates the web environment from the state of the provided application context.
	 * @param applicationContext the application context
	 * @return the web environment the context is running in, or null if not running in a web environment
	 */
	public static MvcEnvironment environmentFor(ApplicationContext applicationContext) {
		if (applicationContext instanceof WebApplicationContext) {
			return MvcEnvironment.SERVLET;
		} else {
			return null;
		}
	}

}

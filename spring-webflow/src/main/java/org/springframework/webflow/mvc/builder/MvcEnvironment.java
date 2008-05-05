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
import org.springframework.core.enums.StaticLabeledEnum;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.portlet.context.ConfigurablePortletApplicationContext;

/**
 * Supported Spring Web MVC environments.
 * 
 * @author Keith Donald
 */
public class MvcEnvironment extends StaticLabeledEnum {

	/**
	 * Spring Web Servlet MVC.
	 */
	public static final MvcEnvironment SERVLET = new MvcEnvironment(0, "servlet");

	/**
	 * Spring Web Portlet MVC.
	 */
	public static final MvcEnvironment PORTLET = new MvcEnvironment(1, "portlet");

	private MvcEnvironment(int code, String label) {
		super(code, label);
	}

	/**
	 * Calculates the web environment from the state of the provided application context.
	 * @param applicationContext the application context
	 * @return the web environment the context is running in, or null if not running in a web environment
	 */
	public static MvcEnvironment environmentFor(ApplicationContext applicationContext) {
		if (ClassUtils.isPresent("javax.portlet.PortletContext") && isPortletApplicationContext(applicationContext)) {
			return MvcEnvironment.PORTLET;
		} else if (applicationContext instanceof WebApplicationContext) {
			return MvcEnvironment.SERVLET;
		} else {
			return null;
		}
	}

	private static boolean isPortletApplicationContext(ApplicationContext applicationContext) {
		return ClassUtils.isPresent("org.springframework.web.portlet.context.ConfigurablePortletApplicationContext")
				&& applicationContext instanceof ConfigurablePortletApplicationContext;
	}

}

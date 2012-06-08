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

import java.lang.reflect.Method;

import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;

/**
 * Utilities when dealing with {@link PortletResponse}s.
 * 
 * @since 2.4.0
 * @author Phillip Webb
 */
class PortletResponseUtils {

	public static void setStatusCode(ResourceResponse response, HttpStatus statusCode) {
		setStatusCode(response, statusCode.value());
	}

	public static void setStatusCode(PortletResponse response, int statusCode) {
		response.setProperty(ResourceResponse.HTTP_STATUS_CODE, String.valueOf(statusCode));
		setStatusCodeForPluto(response, statusCode);
	}

	private static void setStatusCodeForPluto(PortletResponse response, int statusCode) {
		// Work around PLUTO-603
		if (response.getClass().getName().startsWith("org.apache.pluto")) {
			Method servletResponseMethod = ReflectionUtils.findMethod(response.getClass(), "getServletResponse");
			if (servletResponseMethod != null) {
				try {
					ReflectionUtils.makeAccessible(servletResponseMethod);
					HttpServletResponse servletResponse = (HttpServletResponse) servletResponseMethod.invoke(response);
					servletResponse.setStatus(statusCode);
				} catch (Exception e) {
				}
			}
		}
	}
}

/*
 * Copyright 2004-2010 the original author or authors.
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

import javax.faces.context.FacesContext;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.webflow.execution.RequestContext;

/**
 * Helper class to provide information about the JSF runtime environment such as JSF version and implementation.
 * 
 * @author Phil Webb
 */
public class JsfRuntimeInformation {

	/** JSF Version 1.1 */
	public static final int JSF_11 = 0;

	/** JSF Version 1.2 */
	public static final int JSF_12 = 1;

	/** JSF Version 2.0 */
	public static final int JSF_20 = 2;

	private static final int jsfVersion;

	private static final boolean myFacesPresent = ClassUtils.isPresent("org.apache.myfaces.webapp.MyFacesServlet",
			JsfUtils.class.getClassLoader());

	static {
		if (ReflectionUtils.findMethod(FacesContext.class, "isPostback") != null) {
			jsfVersion = JSF_20;
		} else if (ReflectionUtils.findMethod(FacesContext.class, "getELContext") != null) {
			jsfVersion = JSF_12;
		} else {
			jsfVersion = JSF_11;
		}
	}

	public static boolean isAtLeastJsf20() {
		return jsfVersion >= JSF_20;
	}

	public static boolean isAtLeastJsf12() {
		return jsfVersion >= JSF_12;
	}

	public static boolean isLessThanJsf20() {
		return jsfVersion < JSF_20;
	}

	public static boolean isMyFacesPresent() {
		return myFacesPresent;
	}

	public static boolean isPortletRequest(FacesContext context) {
		return context.getExternalContext().getContext().getClass().getName().indexOf("Portlet") != -1;
	}

	public static boolean isPortletRequest(RequestContext context) {
		return (null != ClassUtils.getMethodIfAvailable(context.getExternalContext().getNativeContext().getClass(),
				"getPortletContextName"));
	}

	/**
	 * Returns true if Web Flow supports partial state saving in the current runtime environment.
	 */
	public static boolean isPartialStateSavingSupported() {
		return (JsfRuntimeInformation.isAtLeastJsf20() && (!JsfRuntimeInformation.isMyFacesPresent()));
	}

}
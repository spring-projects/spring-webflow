/*
 * Copyright 2004-2017 the original author or authors.
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
import javax.faces.context.FacesContextWrapper;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Helper class to provide information about the JSF runtime environment such as
 * JSF version and implementation.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class JsfRuntimeInformation {

	private static final ClassLoader CLASSLOADER = JsfRuntimeInformation.class.getClassLoader();


	public static final int JSF_22 = 4;

	public static final int JSF_21 = 3;

	public static final int JSF_20 = 2;

	public static final int JSF_12 = 1;

	public static final int JSF_11 = 0;

	private static final int jsfVersion;

	static {
		if (ReflectionUtils.findMethod(FacesContext.class, "getResourceLibraryContracts") != null) {
			jsfVersion = JSF_22;
		} else if (ReflectionUtils.findMethod(FacesContext.class, "isReleased") != null) {
			jsfVersion = JSF_21;
		} else if (ReflectionUtils.findMethod(FacesContext.class, "isPostback") != null) {
			jsfVersion = JSF_20;
		} else if (ReflectionUtils.findMethod(FacesContext.class, "getELContext") != null) {
			jsfVersion = JSF_12;
		} else {
			jsfVersion = JSF_11;
		}
	}

	private static final boolean mojarraPresent =
			ClassUtils.isPresent("com.sun.faces.context.FacesContextImpl", CLASSLOADER);

	private static final boolean myFacesPresent =
			ClassUtils.isPresent("org.apache.myfaces.webapp.MyFacesServlet", CLASSLOADER);

	private static Boolean myFacesInUse;



	public static boolean isAtLeastJsf22() {
		return jsfVersion >= JSF_22;
	}

	public static boolean isAtLeastJsf21() {
		return jsfVersion >= JSF_21;
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


	public static boolean isMojarraPresent() {
		return mojarraPresent;
	}

	public static boolean isMyFacesPresent() {
		return myFacesPresent;
	}

	public static boolean isMyFacesInUse() {
		if (myFacesInUse == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (facesContext == null) {
				return false;
			}
			while (facesContext instanceof FacesContextWrapper) {
				facesContext = ((FacesContextWrapper) facesContext).getWrapped();
			}
			myFacesInUse = facesContext.getClass().getPackage().getName().startsWith("org.apache.myfaces.");
		}
		return myFacesInUse;
	}

}

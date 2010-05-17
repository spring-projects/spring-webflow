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

import org.springframework.util.ReflectionUtils;

/**
 * Internal helper class to find the version of JSF in use at runtime.
 * 
 * @author Phil Webb
 */
public class JsfVersion {

	/**
	 * JSF Version 1.1
	 */
	public static final int JSF_11 = 0;

	/**
	 * JSF Version 1.2
	 */
	public static final int JSF_12 = 1;

	/**
	 * JSF Version 2.0
	 */
	public static final int JSF_20 = 2;

	private static final int jsfVersion;

	static {
		if (ReflectionUtils.findMethod(FacesContext.class, "isPostback") != null) {
			jsfVersion = JsfVersion.JSF_20;
		} else if (ReflectionUtils.findMethod(FacesContext.class, "getELContext") != null) {
			jsfVersion = JsfVersion.JSF_12;
		} else {
			jsfVersion = JsfVersion.JSF_11;
		}
	}

	public static int getJsfVersion() {
		return jsfVersion;
	}

	public static boolean isAtLeastJsf20() {
		return jsfVersion >= JSF_20;
	}

	public static boolean isAtLeastJsf12() {
		return jsfVersion >= JSF_12;
	}

}
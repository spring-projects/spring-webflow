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

package org.springframework.faces.webflow;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.webflow.execution.RequestContext;

/**
 * Helper class to provide information about the JSF runtime environment such as JSF version and implementation.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class JsfRuntimeInformation {

	/**
	 * JSF Version 1.1
	 *
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static final int JSF_11 = 0;

	/**
	 * JSF Version 1.2
	 *
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static final int JSF_12 = 1;

	/** JSF Version 2.0 */
	public static final int JSF_20 = 2;

	/** JSF Version 2.1 */
	public static final int JSF_21 = 3;

	/** JSF Version 2.2 */
	public static final int JSF_22 = 4;

	private static final int jsfVersion;

	private static final ClassLoader CLASS_LOADER = JsfUtils.class.getClassLoader();

	private static final boolean myFacesPresent = ClassUtils.isPresent("org.apache.myfaces.webapp.MyFacesServlet", CLASS_LOADER);

	private static final boolean myFacesInUse = checkMyFacesContextFactory();

	private static boolean portletPresent = ClassUtils.isPresent("javax.portlet.Portlet", CLASS_LOADER);

	private static boolean springPortletPresent = ClassUtils.isPresent("org.springframework.web.portlet.DispatcherPortlet", CLASS_LOADER);

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

	private static boolean checkMyFacesContextFactory() {
		ClassLoader classLoader = JsfUtils.class.getClassLoader();
		try {
			Class<?> clazz = classLoader.loadClass("org.apache.myfaces.context.FacesContextFactoryImpl");
			Object factory = FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			return clazz.isInstance(factory);
		}
		catch (Throwable ex) {
			return false;
		}
	}

	public static boolean isAtLeastJsf22() {
		return jsfVersion >= JSF_22;
	}

	public static boolean isAtLeastJsf21() {
		return jsfVersion >= JSF_21;
	}

	/**
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static boolean isAtLeastJsf20() {
		return jsfVersion >= JSF_20;
	}

	/**
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static boolean isAtLeastJsf12() {
		return jsfVersion >= JSF_12;
	}

	/**
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static boolean isLessThanJsf20() {
		return jsfVersion < JSF_20;
	}

	public static boolean isMyFacesPresent() {
		return myFacesPresent;
	}

	public static boolean isMyFacesInUse() {
		return myFacesInUse;
	}

	/**
	 * Determines if the container has support for portlets and if Spring MVC portlet support is available
	 *
	 * @return <tt>true</tt> if a portlet environment is detected
	 */
	public static boolean isSpringPortletPresent() {
		return portletPresent && springPortletPresent;
	}

	/**
	 * Determine if the specified {@link FacesContext} is from a portlet request.
	 *
	 * @param context the faces context
	 * @return <tt>true</tt> if the request is from a portlet
	 */
	public static boolean isPortletRequest(FacesContext context) {
		Assert.notNull(context, "Context must not be null");
		return isPortletContext(context.getExternalContext().getContext());
	}

	/**
	 * Determine if the specified {@link RequestContext} is from a portlet request.
	 *
	 * @param context the request context
	 * @return <tt>true</tt> if the request is from a portlet
	 */
	public static boolean isPortletRequest(RequestContext context) {
		Assert.notNull(context, "Context must not be null");
		return isPortletContext(context.getExternalContext().getNativeContext());
	}

	/**
	 * Determine if the specified context object is from portlet.
	 *
	 * @param nativeContext the native context
	 * @return <tt>true</tt> if the context is from a portlet
	 */
	public static boolean isPortletContext(Object nativeContext) {
		Assert.notNull(nativeContext, "Context must not be null");
		return ClassUtils.getMethodIfAvailable(nativeContext.getClass(), "getPortletContextName") != null;
	}
}

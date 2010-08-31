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
package org.springframework.faces.security;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.faces.webflow.JsfRuntimeInformation;

import com.sun.faces.facelets.tag.AbstractTagLibrary;

/**
 * Registers a tag handler for the &lt;authorize&gt; tag and several EL functions that can be used on any component that
 * accepts EL expressions in its attributes. For details on the EL functions see {@link FaceletsAuthorizeTagUtils} or
 * {@link Jsf12FaceletsAuthorizeTagUtils} depending on your environment.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 * @see FaceletsAuthorizeTagHandler
 * @see FaceletsAuthorizeTagUtils
 */
public class SpringSecurityTagLibrary extends AbstractTagLibrary {

	public static final String NAMESPACE = "http://www.springframework.org/security/tags";

	public SpringSecurityTagLibrary() {
		super(NAMESPACE);

		if (JsfRuntimeInformation.isAtLeastJsf20()) {
			this.addTagHandler("authorize", FaceletsAuthorizeTagHandler.class);
		} else {
			this.addTagHandler("authorize", Jsf12FaceletsAuthorizeTagHandler.class);
		}

		try {
			Method[] methods = (JsfRuntimeInformation.isAtLeastJsf20()) ? FaceletsAuthorizeTagUtils.class.getMethods()
					: Jsf12FaceletsAuthorizeTagUtils.class.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (Modifier.isStatic(methods[i].getModifiers())) {
					this.addFunction(methods[i].getName(), methods[i]);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

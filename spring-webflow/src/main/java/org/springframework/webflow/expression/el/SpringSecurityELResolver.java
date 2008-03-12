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
package org.springframework.webflow.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Custom EL resolver that resolves to the Spring Security Principal object for binding expressions prefixed with
 * {@link #SECURITY_PRINCIPAL_VARIABLE_NAME}. For instance "#{currentUser}".
 * 
 * @author Scott Andrews
 */
public class SpringSecurityELResolver extends ELResolver {

	private static final Log logger = LogFactory.getLog(SpringSecurityELResolver.class);

	/**
	 * Name of the security principal variable.
	 */
	public static final String SECURITY_PRINCIPAL_VARIABLE_NAME = "currentUser";

	public Class getCommonPropertyType(ELContext elContext, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class getType(ELContext elContext, Object base, Object property) {
		if (base == null && SECURITY_PRINCIPAL_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return Authentication.class;
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		if (base == null && SECURITY_PRINCIPAL_VARIABLE_NAME.equals(property)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Successfully resolved implicit flow variable '" + property + "'");
			}
			elContext.setPropertyResolved(true);
			if (SecurityContextHolder.getContext() != null) {
				return SecurityContextHolder.getContext().getAuthentication();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		if (base == null && SECURITY_PRINCIPAL_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		if (base == null && SECURITY_PRINCIPAL_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			throw new PropertyNotWritableException("The " + SECURITY_PRINCIPAL_VARIABLE_NAME
					+ " cannot be set with an expression.");
		}
	}

}

/*
 * Copyright 2004-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.security;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.util.StringUtils;

/**
 * Encapsulates the rules for comparing security attributes
 * 
 * @author Scott Andrews
 */
public class SecurityRule {

	/**
	 * Attribute name for the location of the security rule
	 */
	public static final String SECURITY_ATTRIBUTE_NAME = "secured";

	/**
	 * Compare method where any attribute authorization allows access
	 */
	public static final short COMPARISON_ANY = 1;

	/**
	 * Compare method where all attribute authorization allows access
	 */
	public static final short COMPARISON_ALL = 2;

	private Collection<String> attributes;

	private short comparisonType = COMPARISON_ANY;

	private AuthorizationManager<Object> authorizationManager;

	/**
	 * Convert attributes to comma separated String
	 * @param attributes the attributes to convert
	 * @return comma separated String
	 */
	public static String securityAttributesToCommaDelimitedList(Collection<?> attributes) {
		return StringUtils.collectionToDelimitedString(attributes, ", ");
	}

	/**
	 * Convert attributes from comma separated String to Collection
	 * @param attributes the attributes to convert
	 * @return comma parsed Collection
	 */
	public static Collection<String> commaDelimitedListToSecurityAttributes(String attributes) {
		Collection<String> attrs = new HashSet<>();
		for (String attribute : attributes.split(",")) {
			attribute = attribute.trim();
			if (!"".equals(attribute)) {
				attrs.add(attribute);
			}
		}
		return attrs;
	}

	/**
	 * Gets security attributes
	 * @return security attributes
	 */
	public Collection<String> getAttributes() {
		return attributes;
	}

	/**
	 * Sets security attributes
	 * @param attributes security attributes
	 */
	public void setAttributes(Collection<String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Gets comparison type
	 * @return comparison type
	 */
	public short getComparisonType() {
		return comparisonType;
	}

	/**
	 * Sets comparison type
	 * @param comparisonType comparison type
	 */
	public void setComparisonType(short comparisonType) {
		this.comparisonType = comparisonType;
	}

	/**
	 * Return an {@link AuthorizationManager} for this security config based on
	 * {@link AuthorityAuthorizationManager}.
	 */
	@SuppressWarnings("unchecked")
	public AuthorizationManager<Object> getAuthorizationManager() {
		if (this.authorizationManager == null) {
			this.authorizationManager = switch (this.comparisonType) {
				case SecurityRule.COMPARISON_ANY ->
						AuthorityAuthorizationManager.hasAnyAuthority(this.attributes.toArray(new String[0]));
				case SecurityRule.COMPARISON_ALL -> AuthorizationManagers.allOf(this.attributes.stream()
						.map(AuthorityAuthorizationManager::hasAuthority)
						.toArray(AuthorizationManager[]::new));
				default -> throw new IllegalStateException("Unknown SecurityRule match type: " + this.comparisonType);
			};
		}
		return this.authorizationManager;
	}

}

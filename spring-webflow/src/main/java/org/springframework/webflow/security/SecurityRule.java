package org.springframework.webflow.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Encapsulates the rules for comparing security authorities
 * 
 * @author Scott Andrews
 */
public class SecurityRule {

	/**
	 * Attribute name for the location of the security rule
	 */
	public static final String SECURITY_AUTHORITY_ATTRIBUTE_NAME = "secured";

	/**
	 * Compare method where any of the required authorities can match the principal's authorities
	 */
	public static final short COMPARISON_ANY = 1;

	/**
	 * Compare method where all of the required authorities must match the principal's authorities
	 */
	public static final short COMPARISON_ALL = 2;

	private Collection requiredAuthorities;
	private short comparisonType = COMPARISON_ANY;

	/**
	 * Convert authorities to comma separated String
	 * @param authorities the authorities to convert
	 * @return comma separated String
	 */
	public static String convertAuthoritiesToCommaSeparatedString(Collection authorities) {
		StringBuffer str = new StringBuffer();
		Iterator authorityIt = authorities.iterator();
		while (authorityIt.hasNext()) {
			if (str.length() != 0) {
				str.append(", ");
			}
			str.append(authorityIt.next());
		}
		return str.toString();
	}

	/**
	 * Convert authorities from comma separated String to Collection
	 * @param authorities the authorities to convert
	 * @return comma parsed Collection
	 */
	public static Collection convertAuthoritiesFromCommaSeparatedString(String authorities) {
		Collection auths = new HashSet();
		Iterator authorityIt = Arrays.asList(authorities.split(",")).iterator();
		while (authorityIt.hasNext()) {
			String authority = ((String) authorityIt.next()).trim();
			if (!"".equals(authority)) {
				auths.add(authority);
			}
		}
		return auths;
	}

	/**
	 * Gets required authorities
	 * @return required authorities
	 */
	public Collection getRequiredAuthorities() {
		return requiredAuthorities;
	}

	/**
	 * Sets required authorities
	 * @param requiredAuthorities required authorities
	 */
	public void setRequiredAuthorities(Collection requiredAuthorities) {
		this.requiredAuthorities = requiredAuthorities;
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
}

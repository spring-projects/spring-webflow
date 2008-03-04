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
	private short comparisonType;

	/**
	 * Test the required authorities against the principal's authorities
	 * @param principalAuthorities the principal's granted authorities
	 * @return true if authorized
	 */
	public boolean isAuthorized(Collection principalAuthorities) {
		if (getComparisonType() == COMPARISON_ANY) {
			return isAuthorizedAny(principalAuthorities);
		} else if (getComparisonType() == COMPARISON_ALL) {
			return isAuthorizedAll(principalAuthorities);
		} else {
			throw new IllegalStateException("Unknow comparisonType");
		}
	}

	/**
	 * Get authorities that are required but not granted
	 * @param principalAuthorities the principal's granted authorities
	 * @return non granted authorities
	 */
	public Collection getNonGrantedAuthorities(Collection principalAuthorities) {
		Collection nonGrantedAuthorities = new HashSet();
		Iterator authorityIt = getRequiredAuthorities().iterator();
		while (authorityIt.hasNext()) {
			String authority = (String) authorityIt.next();
			if (!principalAuthorities.contains(authority)) {
				nonGrantedAuthorities.add(authority);
			}
		}
		return nonGrantedAuthorities;
	}

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
	 * Test that any of the required authorities match the principal's authorities
	 * @param principalAuthorities the principal's granted authorities
	 * @return true if authorized
	 */
	private boolean isAuthorizedAny(Collection principalAuthorities) {
		boolean authorized = false;
		Iterator authorityIt = principalAuthorities.iterator();
		while (!authorized && authorityIt.hasNext()) {
			if (getRequiredAuthorities().contains(authorityIt.next())) {
				authorized = true;
			}
		}
		return authorized;
	}

	/**
	 * Test that all of the required authorities match the principal's authorities
	 * @param principalAuthorities the principal's granted authorities
	 * @return true if authorized
	 */
	private boolean isAuthorizedAll(Collection principalAuthorities) {
		return principalAuthorities.containsAll(getRequiredAuthorities());
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

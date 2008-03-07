package org.springframework.webflow.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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

	private Collection attributes;
	private short comparisonType = COMPARISON_ANY;

	/**
	 * Convert attributes to comma separated String
	 * @param attributes the attributes to convert
	 * @return comma separated String
	 */
	public static String convertAttributesToCommaSeparatedString(Collection attributes) {
		StringBuffer attrs = new StringBuffer();
		Iterator attributeIt = attributes.iterator();
		while (attributeIt.hasNext()) {
			if (attrs.length() != 0) {
				attrs.append(", ");
			}
			attrs.append(attributeIt.next());
		}
		return attrs.toString();
	}

	/**
	 * Convert attributes from comma separated String to Collection
	 * @param attributes the attributes to convert
	 * @return comma parsed Collection
	 */
	public static Collection convertAttributesFromCommaSeparatedString(String attributes) {
		Collection attrs = new HashSet();
		Iterator attributeIt = Arrays.asList(attributes.split(",")).iterator();
		while (attributeIt.hasNext()) {
			String attribute = ((String) attributeIt.next()).trim();
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
	public Collection getAttributes() {
		return attributes;
	}

	/**
	 * Sets security attributes
	 * @param attributes security attributes
	 */
	public void setAttributes(Collection attributes) {
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
}

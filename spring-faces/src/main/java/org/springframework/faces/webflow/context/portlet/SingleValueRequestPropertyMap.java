package org.springframework.faces.webflow.context.portlet;

import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

/**
 * A {@link Map} for accessing to {@link PortletContext} request properties containing single String values.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.4.0
 * 
 * @see PortletRequest#getProperties(String)
 */
public class SingleValueRequestPropertyMap extends RequestPropertyMap<String> {

	public SingleValueRequestPropertyMap(PortletRequest portletRequest) {
		super(portletRequest);
	}

	protected String getAttribute(String key) {
		return getPortletRequest().getProperty(key);
	}
}

package org.springframework.faces.webflow.context.portlet;

import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

/**
 * A {@link Map} for accessing to {@link PortletContext} request parameters as a String array.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.4.0
 * 
 * @see PortletRequest#getParameterValues(String)
 */
public class MultiValueRequestParameterMap extends RequestParameterMap<String[]> {

	public MultiValueRequestParameterMap(PortletRequest portletRequest) {
		super(portletRequest);
	}

	protected String[] getAttribute(String key) {
		return getPortletRequest().getParameterValues(key);
	}
}

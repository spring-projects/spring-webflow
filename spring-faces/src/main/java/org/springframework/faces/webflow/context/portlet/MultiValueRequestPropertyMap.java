package org.springframework.faces.webflow.context.portlet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

/**
 * A {@link Map} for accessing to {@link PortletContext} request properties as a String array.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @since 2.4.0
 * 
 * @see PortletRequest#getProperties(String)
 */
public class MultiValueRequestPropertyMap extends RequestPropertyMap<String[]> {

	public MultiValueRequestPropertyMap(PortletRequest portletRequest) {
		super(portletRequest);
	}

	protected String[] getAttribute(String key) {
		List<String> list = Collections.list(getPortletRequest().getProperties(key));
		return list.toArray(new String[list.size()]);
	}
}

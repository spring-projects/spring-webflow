/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.faces.webflow.context.portlet;

import java.util.Iterator;

import javax.portlet.PortletContext;

import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * Map backed by a PortletContext for accessing Portlet initialization parameters.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class InitParameterMap extends StringKeyedMapAdapter<String> {

	final private PortletContext portletContext;

	public InitParameterMap(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	@Override
	protected String getAttribute(String key) {
		return portletContext.getInitParameter(key);
	}

	@Override
	protected void setAttribute(String key, String value) {
		throw new UnsupportedOperationException("Cannot set PortletContext InitParameter");
	}

	@Override
	protected void removeAttribute(String key) {
		throw new UnsupportedOperationException("Cannot remove PortletContext InitParameter");
	}

	@Override
	protected Iterator<String> getAttributeNames() {
		return CollectionUtils.toIterator(portletContext.getInitParameterNames());
	}

}

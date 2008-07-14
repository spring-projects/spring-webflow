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
package org.springframework.webflow.engine.model;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.webflow.security.SecurityFlowExecutionListener;

/**
 * Model support for secured elements.
 * <p>
 * Secures a flow, state or transition. The user invoking this element must meet the required attributes otherwise
 * access will be denied.
 * <p>
 * <b>Warning:</b> This model will only configure a security attribute in the definition. The flow execution must also
 * be secured with a SecurityFlowExecutionListener.
 * 
 * @see SecurityFlowExecutionListener
 * @author Scott Andrews
 */
public class SecuredModel extends AbstractModel {

	private String attributes;

	private String match;

	/**
	 * Create a security settings model
	 * @param attributes the security attributes
	 */
	public SecuredModel(String attributes) {
		setAttributes(attributes);
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof SecuredModel)) {
			return false;
		}
		SecuredModel secured = (SecuredModel) model;
		return ObjectUtils.nullSafeEquals(getAttributes(), secured.getAttributes());
	}

	public void merge(Model model) {
		SecuredModel secured = (SecuredModel) model;
		setMatch(merge(getMatch(), secured.getMatch()));
	}

	/**
	 * @return the attributes
	 */
	public String getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(String attributes) {
		if (StringUtils.hasText(attributes)) {
			this.attributes = attributes;
		} else {
			this.attributes = null;
		}
	}

	/**
	 * @return the match
	 */
	public String getMatch() {
		return match;
	}

	/**
	 * @param match the match to set
	 */
	public void setMatch(String match) {
		if (StringUtils.hasText(match)) {
			this.match = match;
		} else {
			this.match = null;
		}
	}
}

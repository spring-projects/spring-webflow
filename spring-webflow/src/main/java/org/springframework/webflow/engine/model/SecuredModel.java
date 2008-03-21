/*
 * Copyright 2004-2007 the original author or authors.
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

	/**
	 * Create a security settings model
	 * @param attributes the security attributes
	 * @param match the type of matching for the attributes
	 */
	public SecuredModel(String attributes, String match) {
		setAttributes(attributes);
		setMatch(match);
	}

	/**
	 * Merge properties
	 * @param model the secured to merge into this secured
	 */
	public void merge(Model model) {
		if (isMergeableWith(model)) {
			SecuredModel secured = (SecuredModel) model;
			setMatch(merge(getMatch(), secured.getMatch()));
		}
	}

	/**
	 * Tests if the model is able to be merged with this secured attribute
	 * @param model the model to test
	 */
	public boolean isMergeableWith(Model model) {
		if (model == null) {
			return false;
		}
		if (!(model instanceof SecuredModel)) {
			return false;
		}
		SecuredModel secured = (SecuredModel) model;
		return ObjectUtils.nullSafeEquals(getAttributes(), secured.getAttributes());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof SecuredModel)) {
			return false;
		}
		SecuredModel secured = (SecuredModel) obj;
		if (secured == null) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getAttributes(), secured.getAttributes())) {
			return false;
		} else if (!ObjectUtils.nullSafeEquals(getMatch(), secured.getMatch())) {
			return false;
		} else {
			return true;
		}
	}

	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(getAttributes()) * 27 + ObjectUtils.nullSafeHashCode(getMatch()) * 27;
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

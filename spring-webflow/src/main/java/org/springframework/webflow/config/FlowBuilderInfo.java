package org.springframework.webflow.config;

import java.util.Collections;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * A low-level pointer to a flow definition that will be registered in a flow registry and built by a concrete flow
 * builder implementation class.
 * 
 * @author Keith Donald
 */
class FlowBuilderInfo {

	/**
	 * The id to assign to the flow definition.
	 */
	private String id;

	/**
	 * The fully-qualified flow builder implementation class.
	 */
	private String className;

	/**
	 * Attributes to assign to the flow definition.
	 */
	private Set attributes;

	public FlowBuilderInfo(String id, String className, Set attributes) {
		Assert.hasText(className, "The fully-qualified FlowBuilder class name is required");
		this.className = className;
		setId(id);
		this.attributes = (attributes != null ? attributes : Collections.EMPTY_SET);
	}

	private void setId(String id) {
		if (StringUtils.hasText(id)) {
			this.id = id;
		} else {
			this.id = StringUtils.uncapitalize(StringUtils.delete(ClassUtils.getShortName(className), "FlowBuilder"));
		}
	}

	public String getId() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public Set getAttributes() {
		return attributes;
	}
}

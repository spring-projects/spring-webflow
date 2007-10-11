package org.springframework.webflow.config;

import java.util.Collections;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * A low-level pointer to a flow definition that will be registered in a registry and built from an external file
 * resource.
 * 
 * @author Keith Donald
 */
class FlowLocation {

	/**
	 * The id to assign to the flow definition.
	 */
	private String id;

	/**
	 * The string-encoded path to the flow definition file resource.
	 */
	private String path;

	/**
	 * Attributes to assign to the flow definition.
	 */
	private Set attributes;

	public FlowLocation(String id, String path, Set attributes) {
		Assert.hasText(path, "The path is required");
		this.id = id;
		this.path = path;
		this.attributes = (attributes != null ? attributes : Collections.EMPTY_SET);
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public Set getAttributes() {
		return attributes;
	}
}

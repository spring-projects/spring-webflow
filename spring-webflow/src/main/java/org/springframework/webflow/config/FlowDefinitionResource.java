package org.springframework.webflow.config;

import org.springframework.core.io.Resource;
import org.springframework.webflow.core.collection.AttributeMap;

/**
 * An abstract representation of an externalized flow definition resource. Holds the data necessary to build a flow
 * definition from an external file, and register the flow definition in a flow definition registry.
 * 
 * Flow definition resources are created by a {@link FlowDefinitionResourceFactory}.
 * 
 * @author Keith Donald
 * @see FlowDefinitionResource
 */
public class FlowDefinitionResource {

	private String id;

	private Resource path;

	private AttributeMap attributes;

	FlowDefinitionResource(String flowId, Resource path, AttributeMap attributes) {
		this.id = flowId;
		this.path = path;
		this.attributes = attributes;
	}

	/**
	 * Returns the identifier to be assigned to the flow definition.
	 * @return the flow definition identifier
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the path to the flow definition resource.
	 * @return the path location
	 */
	public Resource getPath() {
		return path;
	}

	/**
	 * Returns attributes to assign the flow definition.
	 * @return flow definition attributes
	 */
	public AttributeMap getAttributes() {
		return attributes;
	}

	public String toString() {
		return path.toString();
	}
}

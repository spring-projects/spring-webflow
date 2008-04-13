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

	/**
	 * Creates a new flow definition resource
	 * @param flowId the flow id
	 * @param path the location of the resource
	 * @param attributes meta-attributes describing the flow resource
	 */
	public FlowDefinitionResource(String flowId, Resource path, AttributeMap attributes) {
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

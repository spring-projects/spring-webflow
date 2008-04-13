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

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
package org.springframework.webflow.util;

import org.springframework.core.io.Resource;

/**
 * Simple interface for all objects (typically flow builders) that hold on to a resource defining a flow (e.g. an XML
 * file). Provides a way to access information about the underlying resource like the last modified date.
 * 
 * @see org.springframework.webflow.engine.builder.FlowBuilder
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public interface ResourceHolder {

	/**
	 * Returns the flow definition resource held by this holder.
	 */
	public Resource getResource();
}
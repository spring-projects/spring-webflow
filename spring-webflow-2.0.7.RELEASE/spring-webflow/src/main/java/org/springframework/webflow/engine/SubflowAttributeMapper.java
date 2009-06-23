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
package org.springframework.webflow.engine;

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;

/**
 * A strategy interface used by a subflow state to map subflow input and output attributes.
 * @author Keith Donald
 */
public interface SubflowAttributeMapper {

	/**
	 * Create a map of attributes that should be passed as <i>input</i> to a subflow.
	 * @param context the current request execution context
	 * @return a map of attributes to pass as input
	 */
	public MutableAttributeMap createSubflowInput(RequestContext context);

	/**
	 * Map output attributes of an ended subflow flow to the resuming parent flow.
	 * @param output the output attributes returned by the ended subflow
	 * @param context the current request execution context, which gives access to the parent flow scope
	 */
	public void mapSubflowOutput(AttributeMap output, RequestContext context);
}
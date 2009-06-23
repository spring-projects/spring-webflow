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
package org.springframework.webflow.engine.model.registry;

import org.springframework.webflow.engine.model.FlowModel;

/**
 * A runtime service locator interface for retrieving flow definitions by <code>id</code>. Flow locators are needed
 * by flow executors at runtime to retrieve flow models to support loading flow definitions.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Scott Andrews
 */
public interface FlowModelLocator {

	/**
	 * Lookup the flow model with the specified id.
	 * @param id the flow model identifier
	 * @return the flow mode
	 * @throws NoSuchFlowModelException when the flow model with the specified id does not exist
	 */
	public FlowModel getFlowModel(String id) throws NoSuchFlowModelException;
}
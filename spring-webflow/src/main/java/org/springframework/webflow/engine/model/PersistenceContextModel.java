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

/**
 * Model support for persistence context elements.
 * <p>
 * Allocates a persistence context when this flow starts. The persistence context is closed when the flow ends. If the
 * flow ends by reaching a "commit" end-state, changes made to managed persistent entities during the course of flow
 * execution are flushed to the database in a transaction.
 * <p>
 * The persistence context can be referenced from within this flow by the "entityManager" variable.
 * 
 * @author Scott Andrews
 */
public class PersistenceContextModel extends AbstractModel {

	/**
	 * Create a persistence context model
	 */
	public PersistenceContextModel() {
	}

	public boolean isMergeableWith(Model model) {
		return false;
	}

	public void merge(Model model) {

	}

}

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
 * Interface defining models. All models must be able to handle merging of their content with an eligible model.
 * 
 * @author Scott Andrews
 */
public interface Model {

	/**
	 * Determine if the model is able to be merged into the current model
	 * @param model the model to compare
	 * @return true if able to merge
	 */
	public boolean isMergeableWith(Model model);

	/**
	 * Merge the model into the current model
	 * @param model the model to merge with
	 */
	public void merge(Model model);

}

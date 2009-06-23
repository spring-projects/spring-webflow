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
package org.springframework.webflow.core;

import org.springframework.webflow.core.collection.MutableAttributeMap;

/**
 * An interface to be implemented by objects that are annotated with attributes they wish to expose to clients.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public interface Annotated {

	/**
	 * Returns a short summary of this object, suitable for display as an icon caption or tool tip.
	 * @return the caption
	 */
	public String getCaption();

	/**
	 * Returns a longer, more detailed description of this object.
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Returns a attribute map containing the attributes annotating this object. These attributes provide descriptive
	 * characteristics or properties that may affect object behavior.
	 * @return the attribute map
	 */
	public MutableAttributeMap getAttributes();

}
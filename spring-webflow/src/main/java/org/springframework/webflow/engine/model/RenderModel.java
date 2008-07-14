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

import org.springframework.util.StringUtils;

/**
 * Model support for render actions.
 * <p>
 * Requests that the next view render a fragment of content. Multiple fragments may be specified using a comma
 * delimiter.
 * 
 * @author Scott Andrews
 */
public class RenderModel extends AbstractActionModel {

	private String fragments;

	/**
	 * Create a render action model
	 * @param fragments the fragments to render
	 */
	public RenderModel(String fragments) {
		setFragments(fragments);
	}

	/**
	 * @return the fragments
	 */
	public String getFragments() {
		return fragments;
	}

	/**
	 * @param fragments the fragments to set
	 */
	public void setFragments(String fragments) {
		if (StringUtils.hasText(fragments)) {
			this.fragments = fragments;
		} else {
			this.fragments = null;
		}
	}
}

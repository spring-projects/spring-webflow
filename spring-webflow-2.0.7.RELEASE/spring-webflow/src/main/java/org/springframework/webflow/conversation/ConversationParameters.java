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
package org.springframework.webflow.conversation;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

/**
 * Simple parameter object for clumping together input needed to begin a new conversation.
 * 
 * @author Keith Donald
 */
public class ConversationParameters implements Serializable {

	/**
	 * The conversation name.
	 */
	private String name;

	/**
	 * The conversation caption.
	 */
	private String caption;

	/**
	 * The conversation description.
	 */
	private String description;

	/**
	 * Creates new conversation input parameters.
	 * @param name the name of the conversation
	 * @param caption a short description
	 * @param description a long description
	 */
	public ConversationParameters(String name, String caption, String description) {
		this.name = name;
		this.caption = caption;
		this.description = description;
	}

	/**
	 * Returns the name of the conversation.
	 * @return the conversation name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the short description.
	 * @return the conversation caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Returns the long description.
	 * @return the description.
	 */
	public String getDescription() {
		return description;
	}

	public String toString() {
		return new ToStringCreator(this).append("name", name).toString();
	}
}
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
package org.springframework.webflow.mvc.view;

import org.springframework.binding.mapping.MappingResults;
import org.springframework.core.style.ToStringCreator;

/**
 * Holder class for passing view state through a redirect.
 * 
 * @author Scott Andrews
 */
public class ViewActionStateHolder {

	public static final String KEY = "webflowViewActionStateHolder";

	private String eventId;

	private MappingResults mappingResults;

	public ViewActionStateHolder(String eventId, MappingResults mappingResults) {
		this.eventId = eventId;
		this.mappingResults = mappingResults;
	}

	public String getEventId() {
		return eventId;
	}

	public MappingResults getMappingResults() {
		return mappingResults;
	}

	public String toString() {
		return new ToStringCreator(this).append("eventId", eventId).append("mappingResults", mappingResults).toString();
	}

}
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
package org.springframework.webflow.mvc.portlet;

import org.springframework.binding.mapping.MappingResults;

/**
 * Holder class for mapping results to pass them from an ActionRequest to a RenderRequest
 * 
 * @author Scott Andrews
 */
class MappingResultsHolder {

	static final String MAPPING_RESULTS_HOLDER_KEY = "org.springframework.webflow.mvc.portlet.MAPPING_RESULTS_HOLDER";

	private String eventId;

	private MappingResults mappingResults;

	private boolean viewErrors;

	public MappingResultsHolder(String eventId, MappingResults mappingResults, boolean viewErrors) {
		this.eventId = eventId;
		this.mappingResults = mappingResults;
		this.viewErrors = viewErrors;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public MappingResults getMappingResults() {
		return mappingResults;
	}

	public void setMappingResults(MappingResults mappingResults) {
		this.mappingResults = mappingResults;
	}

	public boolean getViewErrors() {
		return viewErrors;
	}

	public void setViewErrors(boolean viewErrors) {
		this.viewErrors = viewErrors;
	}

}

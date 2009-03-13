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
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;

class TestSubflowAttributeMapper implements SubflowAttributeMapper {
	public MutableAttributeMap createSubflowInput(RequestContext context) {
		LocalAttributeMap inputMap = new LocalAttributeMap();
		inputMap.put("childInputAttribute", context.getFlowScope().get("parentInputAttribute"));
		return inputMap;
	}

	public void mapSubflowOutput(AttributeMap subflowOutput, RequestContext context) {
		MutableAttributeMap parentAttributes = context.getFlowExecutionContext().getActiveSession().getScope();
		parentAttributes.put("parentOutputAttribute", subflowOutput.get("childInputAttribute"));
	}
}
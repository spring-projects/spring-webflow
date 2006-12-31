/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.samples.itemlist;

import java.util.Collection;

import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class AddItemAction extends AbstractAction {

	protected Event doExecute(RequestContext context) throws Exception {
		Collection list = context.getFlowScope().getRequiredCollection("list");
		String data = context.getRequestParameters().get("data");
		if (data != null && data.length() > 0) {
			list.add(data);
		}

		try {
			// add a bit of artificial think time
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
		}

		return success();
	}
}
/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.engine.builder;

import junit.framework.Assert;

import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Test action used by some unit tests.
 * 
 * @author Erwin Vervaet
 */
public class ParameterizationTestAction extends AbstractAction {

	protected Event doExecute(RequestContext context) throws Exception {
		if ("flowA".equals(context.getActiveFlow().getId())) {
			Flow flowA = (Flow)context.getActiveFlow();
			Assert.assertEquals(2, flowA.getAttributes().size());
			Assert.assertEquals("A", flowA.getAttributes().get("name"));
			Assert.assertEquals("someValue", flowA.getAttributes().get("someKey"));
			Assert.assertNull(flowA.getAttributes().get("someOtherKey"));
		}
		else if ("flowB".equals(context.getActiveFlow().getId())) {
			Flow flowB = (Flow)context.getActiveFlow();
			Assert.assertEquals(2, flowB.getAttributes().size());
			Assert.assertEquals("B", flowB.getAttributes().get("name"));
			Assert.assertEquals("someOtherValue", flowB.getAttributes().get("someOtherKey"));
			Assert.assertNull(flowB.getAttributes().get("someKey"));
		}
		else {
			throw new IllegalStateException();
		}
		return success();
	}

}

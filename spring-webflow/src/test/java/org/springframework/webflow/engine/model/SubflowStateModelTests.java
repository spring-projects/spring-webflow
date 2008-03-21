/*
 * Copyright 2004-2007 the original author or authors.
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

import junit.framework.TestCase;

/**
 * Unit tests for {@link SubflowStateModel}.
 */
public class SubflowStateModelTests extends TestCase {

	public void testMerge() {
		SubflowStateModel child = new SubflowStateModel("child", "childflow");
		SubflowStateModel parent = new SubflowStateModel("parent", "parentflow");
		child.merge(parent);
		assertEquals("child", child.getId());
	}

	public void testMergeNullParent() {
		SubflowStateModel child = new SubflowStateModel("child", "childflow");
		SubflowStateModel parent = null;
		child.merge(parent);
		assertEquals("child", child.getId());
	}

	public void testMergeOverrideMatch() {
		SubflowStateModel child = new SubflowStateModel("child", "childflow");
		SubflowStateModel parent = new SubflowStateModel("child", "parentflow");
		parent.addInput(new InputModel("inname", "invalue"));
		child.merge(parent);
		assertEquals(1, child.getInputs().size());
	}

	public void testMergeOverrideMatchFailed() {
		SubflowStateModel child = new SubflowStateModel("child", "childflow");
		SubflowStateModel parent = new SubflowStateModel("parent", "parentflow");
		parent.addInput(new InputModel("inname", "invalue"));
		child.merge(parent);
		assertEquals(null, child.getInputs());
	}

}

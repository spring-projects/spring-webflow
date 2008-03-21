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

import org.springframework.webflow.engine.model.InputModel;

import junit.framework.TestCase;

/**
 * Unit tests for {@link InputModel}.
 */
public class InputModelTests extends TestCase {

	public void testMerge() {
		InputModel child = new InputModel("child", "childvalue");
		InputModel parent = new InputModel("parent", "parentvalue");
		child.merge(parent);
		assertEquals("childvalue", child.getValue());
	}

	public void testMergeNullParent() {
		InputModel child = new InputModel("child", "childvalue");
		InputModel parent = null;
		child.merge(parent);
		assertEquals("childvalue", child.getValue());
	}

	public void testMergeOverrideMatch() {
		InputModel child = new InputModel("child", "childvalue");
		InputModel parent = new InputModel("child", "childvalue");
		parent.setType("long");
		child.merge(parent);
		assertEquals("long", child.getType());
	}

	public void testMergeOverrideMatchFailed() {
		InputModel child = new InputModel("child", "childvalue");
		InputModel parent = new InputModel("parent", "parentvalue");
		parent.setType("long");
		child.merge(parent);
		assertEquals(null, child.getType());
	}

}

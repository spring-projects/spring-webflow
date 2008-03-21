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

import org.springframework.webflow.engine.model.SetModel;

import junit.framework.TestCase;

/**
 * Unit tests for {@link SetModel}.
 */
public class SetModelTests extends TestCase {

	public void testMerge() {
		SetModel child = new SetModel("child", "childvalue");
		SetModel parent = new SetModel("parent", "parentvalue");
		child.merge(parent);
		assertEquals("child", child.getName());
	}

	public void testMergeNullParent() {
		SetModel child = new SetModel("child", "childvalue");
		SetModel parent = null;
		child.merge(parent);
		assertEquals("child", child.getName());
	}

	public void testMergeOverrideMatch() {
		SetModel child = new SetModel("child", "childvalue");
		SetModel parent = new SetModel("child", "childvalue", "childtype");
		child.merge(parent);
		assertEquals("childtype", child.getType());
	}

	public void testMergeOverrideMatchFailed() {
		SetModel child = new SetModel("child", "childvalue");
		SetModel parent = new SetModel("parent", "parentvalue", "parenttype");
		child.merge(parent);
		assertEquals(null, child.getType());
	}

}

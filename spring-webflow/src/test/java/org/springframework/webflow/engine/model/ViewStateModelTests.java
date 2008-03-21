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

import org.springframework.webflow.engine.model.ViewStateModel;

import junit.framework.TestCase;

/**
 * Unit tests for {@link ViewStateModel}.
 */
public class ViewStateModelTests extends TestCase {

	public void testMerge() {
		ViewStateModel child = new ViewStateModel("child", "childview");
		ViewStateModel parent = new ViewStateModel("parent", "parentview");
		child.merge(parent);
		assertEquals("childview", child.getView());
	}

	public void testMergeNullParent() {
		ViewStateModel child = new ViewStateModel("child", "childview");
		ViewStateModel parent = null;
		child.merge(parent);
		assertEquals("childview", child.getView());
	}

	public void testMergeOverrideMatch() {
		ViewStateModel child = new ViewStateModel("child");
		ViewStateModel parent = new ViewStateModel("child", "parentview");
		child.merge(parent);
		assertEquals("parentview", child.getView());
	}

	public void testMergeOverrideMatchFailed() {
		ViewStateModel child = new ViewStateModel("child");
		ViewStateModel parent = new ViewStateModel("parent", "parentview");
		child.merge(parent);
		assertEquals(null, child.getView());
	}

}

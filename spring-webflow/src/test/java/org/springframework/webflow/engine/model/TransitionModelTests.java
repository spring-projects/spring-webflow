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

import org.springframework.webflow.engine.model.TransitionModel;

import junit.framework.TestCase;

/**
 * Unit tests for {@link TransitionModel}.
 */
public class TransitionModelTests extends TestCase {

	public void testMerge() {
		TransitionModel child = new TransitionModel("child");
		TransitionModel parent = new TransitionModel("parent");
		child.merge(parent);
		assertEquals("child", child.getOn());
	}

	public void testMergeNullParent() {
		TransitionModel child = new TransitionModel("child");
		TransitionModel parent = null;
		child.merge(parent);
		assertEquals("child", child.getOn());
	}

	public void testMergeOverrideMatch() {
		TransitionModel child = new TransitionModel("child");
		TransitionModel parent = new TransitionModel("child", "end");
		child.merge(parent);
		assertEquals("end", child.getTo());
	}

	public void testMergeOverrideMatchFailed() {
		TransitionModel child = new TransitionModel("child");
		TransitionModel parent = new TransitionModel("parent", "end");
		child.merge(parent);
		assertEquals(null, child.getTo());
	}

}

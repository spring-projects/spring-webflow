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
package org.springframework.webflow.engine.model;

import junit.framework.TestCase;

/**
 * Unit tests for {@link DecisionStateModel}.
 */
public class DecisionStateModelTests extends TestCase {

	public void testMergeable() {
		DecisionStateModel child = new DecisionStateModel("child");
		assertTrue(child.isMergeableWith(child));
	}

	public void testNotMergeable() {
		DecisionStateModel child = new DecisionStateModel("child");
		DecisionStateModel parent = new DecisionStateModel("parent");
		assertFalse(child.isMergeableWith(parent));
	}

	public void testNotMergeableWithNull() {
		DecisionStateModel child = new DecisionStateModel("child");
		assertFalse(child.isMergeableWith(null));
	}

	public void testMerge() {
		DecisionStateModel child = new DecisionStateModel("child");
		DecisionStateModel parent = new DecisionStateModel("child");
		parent.setSecured(new SecuredModel("secured"));
		child.merge(parent);
		assertNotNull(child.getSecured());
	}

}

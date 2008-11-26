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
 * Unit tests for {@link ActionStateModel}.
 */
public class ActionStateModelTests extends TestCase {

	public void testMergeable() {
		ActionStateModel child = new ActionStateModel("child");
		assertTrue(child.isMergeableWith(child));
	}

	public void testNotMergeable() {
		ActionStateModel child = new ActionStateModel("child");
		ActionStateModel parent = new ActionStateModel("parent");
		assertFalse(child.isMergeableWith(parent));
	}

	public void testNotMergeableWithNull() {
		ActionStateModel child = new ActionStateModel("child");
		assertFalse(child.isMergeableWith(null));
	}

	public void testMerge() {
		ActionStateModel child = new ActionStateModel("child");
		ActionStateModel parent = new ActionStateModel("child");
		parent.setSecured(new SecuredModel("secured"));
		child.merge(parent);
		assertNotNull(child.getSecured());
	}

}

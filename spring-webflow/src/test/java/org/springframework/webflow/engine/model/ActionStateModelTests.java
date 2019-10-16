/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.engine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ActionStateModel}.
 */
public class ActionStateModelTests {

	@Test
	public void testMergeable() {
		ActionStateModel child = new ActionStateModel("child");
		assertTrue(child.isMergeableWith(child));
	}

	@Test
	public void testNotMergeable() {
		ActionStateModel child = new ActionStateModel("child");
		ActionStateModel parent = new ActionStateModel("parent");
		assertFalse(child.isMergeableWith(parent));
	}

	@Test
	public void testNotMergeableWithNull() {
		ActionStateModel child = new ActionStateModel("child");
		assertFalse(child.isMergeableWith(null));
	}

	@Test
	public void testMerge() {
		ActionStateModel child = new ActionStateModel("child");
		ActionStateModel parent = new ActionStateModel("parent");

		LinkedList<AbstractActionModel> actions = new LinkedList<>();
		EvaluateModel eval = new EvaluateModel("foo.bar");
		actions.add(eval);
		parent.setActions(actions);

		parent.setSecured(new SecuredModel("secured"));
		child.merge(parent);

		assertNotNull(child.getSecured());
		assertEquals("foo.bar", ((EvaluateModel) child.getActions().get(0)).getExpression());
	}

}

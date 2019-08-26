/*
 * Copyright 2004-2008 the original author or authors.
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TransitionModel}.
 */
public class TransitionModelTests {

	@Test
	public void testMergeable() {
		TransitionModel child = new TransitionModel();
		child.setOn("event");
		TransitionModel parent = new TransitionModel();
		parent.setOn("event");
		assertTrue(child.isMergeableWith(parent));
	}

	@Test
	public void testMergeableOnException() {
		TransitionModel child = new TransitionModel();
		child.setOnException("expception");
		TransitionModel parent = new TransitionModel();
		parent.setOnException("exception");
		assertTrue(child.isMergeableWith(child));
	}

	@Test
	public void testNotMergeable() {
		TransitionModel child = new TransitionModel();
		child.setOn("child");
		TransitionModel parent = new TransitionModel();
		parent.setOn("parent");
		assertFalse(child.isMergeableWith(parent));
	}

	@Test
	public void testNotMergeableOnException() {
		TransitionModel child = new TransitionModel();
		child.setOnException("child");
		TransitionModel parent = new TransitionModel();
		parent.setOnException("parent");
		assertFalse(child.isMergeableWith(parent));
	}

	@Test
	public void testNotMergeableWithNull() {
		TransitionModel child = new TransitionModel();
		assertFalse(child.isMergeableWith(null));
	}

	@Test
	public void testMerge() {
		TransitionModel child = new TransitionModel();
		child.setOn("child");
		TransitionModel parent = new TransitionModel();
		parent.setOn("child");
		parent.setTo("end");
		child.merge(parent);
		assertEquals("end", child.getTo());
	}

}

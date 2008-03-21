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

import org.springframework.webflow.engine.model.IfModel;

import junit.framework.TestCase;

/**
 * Unit tests for {@link IfModel}.
 */
public class IfModelTests extends TestCase {

	public void testMerge() {
		IfModel child = new IfModel("child", "childthen");
		IfModel parent = new IfModel("parent", "parentthen");
		child.merge(parent);
		assertEquals("childthen", child.getThen());
	}

	public void testMergeNullParent() {
		IfModel child = new IfModel("child", "childthen");
		IfModel parent = null;
		child.merge(parent);
		assertEquals("childthen", child.getThen());
	}

	public void testMergeOverrideMatch() {
		IfModel child = new IfModel("child", "childthen");
		IfModel parent = new IfModel("child", "childthen", "childelse");
		child.merge(parent);
		assertEquals("childelse", child.getElse());
	}

	public void testMergeOverrideMatchFailed() {
		IfModel child = new IfModel("child", "childthen");
		IfModel parent = new IfModel("parent", "parentthen", "parentelse");
		child.merge(parent);
		assertEquals(null, child.getElse());
	}

}

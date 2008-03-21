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

import org.springframework.webflow.engine.model.EvaluateModel;

import junit.framework.TestCase;

/**
 * Unit tests for {@link EvaluateModel}.
 */
public class EvaluateModelTests extends TestCase {

	public void testMerge() {
		EvaluateModel child = new EvaluateModel("child");
		EvaluateModel parent = new EvaluateModel("parent");
		child.merge(parent);
		assertEquals("child", child.getExpression());
	}

	public void testMergeNullParent() {
		EvaluateModel child = new EvaluateModel("child");
		EvaluateModel parent = null;
		child.merge(parent);
		assertEquals("child", child.getExpression());
	}

	public void testMergeOverrideMatch() {
		EvaluateModel child = new EvaluateModel("child");
		EvaluateModel parent = new EvaluateModel("child", "end");
		child.merge(parent);
		assertEquals("end", child.getResult());
	}

	public void testMergeOverrideMatchFailed() {
		EvaluateModel child = new EvaluateModel("child");
		EvaluateModel parent = new EvaluateModel("parent", "end");
		child.merge(parent);
		assertEquals(null, child.getResult());
	}

}

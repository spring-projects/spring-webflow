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

import java.util.LinkedList;

import junit.framework.TestCase;

/**
 * Unit tests for {@link AbstractModel}.
 */
public class AbstractModelTests extends TestCase {

	public void testStringMerge() {
		AbstractModel obj = new PersistenceContextModel();
		String child = "child";
		String parent = "parent";
		assertEquals("child", obj.merge(child, parent));
	}

	public void testStringMergeNullParent() {
		AbstractModel obj = new PersistenceContextModel();
		String child = "child";
		String parent = null;
		assertEquals("child", obj.merge(child, parent));
	}

	public void testStringMergeNullChild() {
		AbstractModel obj = new PersistenceContextModel();
		String child = null;
		String parent = "parent";
		assertEquals("parent", obj.merge(child, parent));
	}

	public void testStringMergeNulls() {
		AbstractModel obj = new PersistenceContextModel();
		String child = null;
		String parent = null;
		assertEquals(null, obj.merge(child, parent));
	}

	public void testListMerge() {
		AbstractModel obj = new PersistenceContextModel();
		LinkedList child = new LinkedList();
		child.add(new SecuredModel("1"));
		LinkedList parent = new LinkedList();
		parent.add(new SecuredModel("2"));
		LinkedList result = obj.merge(child, parent);
		assertEquals(2, result.size());
		assertEquals("1", ((SecuredModel) result.get(0)).getAttributes());
		assertEquals("2", ((SecuredModel) result.get(1)).getAttributes());
	}

	public void testListMergeNullParent() {
		AbstractModel obj = new PersistenceContextModel();
		LinkedList child = new LinkedList();
		child.add("1");
		LinkedList parent = null;
		LinkedList result = obj.merge(child, parent);
		assertEquals(1, result.size());
		assertEquals("1", result.get(0));
	}

	public void testListMergeNullChild() {
		AbstractModel obj = new PersistenceContextModel();
		LinkedList child = null;
		LinkedList parent = new LinkedList();
		parent.add("2");
		LinkedList result = obj.merge(child, parent);
		assertEquals(1, result.size());
		assertEquals("2", result.get(0));
	}

	public void testListMergeNulls() {
		AbstractModel obj = new PersistenceContextModel();
		LinkedList child = null;
		LinkedList parent = null;
		LinkedList result = obj.merge(child, parent);
		assertEquals(null, result);
	}
}

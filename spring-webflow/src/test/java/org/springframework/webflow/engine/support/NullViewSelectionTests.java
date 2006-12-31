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
package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.webflow.engine.NullViewSelector;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.test.MockRequestContext;

public class NullViewSelectionTests extends TestCase {

	private MockRequestContext context = new MockRequestContext();
	
	public void testMakeSelection() {
		assertEquals(ViewSelection.NULL_VIEW, NullViewSelector.INSTANCE.makeEntrySelection(context));
	}

	public void testMakeRefreshSelection() {
		assertEquals(ViewSelection.NULL_VIEW, NullViewSelector.INSTANCE.makeRefreshSelection(context));
	}
}

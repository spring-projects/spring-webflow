/*
 * Copyright 2002-2006 the original author or authors.
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

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

public class BeanFactoryFlowVariableTests extends TestCase {
	private MockRequestContext context = new MockRequestContext();

	public void testCreateValidFlowVariable() {
		StaticApplicationContext beanFactory = new StaticApplicationContext();
		beanFactory.registerPrototype("bean", Object.class);
		BeanFactoryFlowVariable variable = new BeanFactoryFlowVariable("var", "bean", beanFactory, ScopeType.FLOW);
		variable.create(context);
		context.getFlowScope().getRequired("var");
	}
}
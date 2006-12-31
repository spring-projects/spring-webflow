/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.engine;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.ExternalRedirectSelector;

public class SimpleFlow extends Flow {
	public SimpleFlow() {
		super("simpleFlow");

		ViewState state1 = new ViewState(this, "view");
		state1.setViewSelector(new ApplicationViewSelector(new StaticExpression("view")));
		state1.getTransitionSet().add(new Transition(to("end")));

		EndState state2 = new EndState(this, "end");
		state2.setViewSelector(new ExternalRedirectSelector(new StaticExpression("confirm")));
	}

	protected TargetStateResolver to(String stateId) {
		return new DefaultTargetStateResolver(stateId);
	}

}
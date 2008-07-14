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

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Model support for if elements.
 * <p>
 * Defines a boolean expression to evaluate a target state to transition to if that expression evaluates to true.
 * Optionally, this element may define an 'else' attribute to define a state to transition to if the expression
 * evaluates to false.
 * 
 * @author Scott Andrews
 */
public class IfModel extends AbstractModel {

	private String test;

	private String then;

	private String elze;

	/**
	 * Create an if model
	 * @param test the boolean condition to test
	 * @param then the state to transition to if the boolean expression evaluates to true
	 */
	public IfModel(String test, String then) {
		setTest(test);
		setThen(then);
	}

	public boolean isMergeableWith(Model model) {
		if (!(model instanceof IfModel)) {
			return false;
		}
		IfModel conditional = (IfModel) model;
		return ObjectUtils.nullSafeEquals(getTest(), conditional.getTest());
	}

	public void merge(Model model) {
		IfModel conditional = (IfModel) model;
		setThen(merge(getThen(), conditional.getThen()));
		setElse(merge(getElse(), conditional.getElse()));
	}

	/**
	 * @return the test
	 */
	public String getTest() {
		return test;
	}

	/**
	 * @param test the test to set
	 */
	public void setTest(String test) {
		if (StringUtils.hasText(test)) {
			this.test = test;
		} else {
			this.test = null;
		}
	}

	/**
	 * @return the then
	 */
	public String getThen() {
		return then;
	}

	/**
	 * @param then the then to set
	 */
	public void setThen(String then) {
		if (StringUtils.hasText(then)) {
			this.then = then;
		} else {
			this.then = null;
		}
	}

	/**
	 * @return the else
	 */
	public String getElse() {
		return elze;
	}

	/**
	 * @param elze the else to set
	 */
	public void setElse(String elze) {
		if (StringUtils.hasText(elze)) {
			this.elze = elze;
		} else {
			this.elze = null;
		}
	}
}

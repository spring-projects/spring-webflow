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
package org.springframework.binding.expression.el;

import java.util.ArrayList;
import java.util.List;

public class TestBean {

	private String value = "foo";
	private int maximum = 2;
	private TestBean bean;
	private List<String> list = new ArrayList<>();

	public TestBean() {
		initList();
	}

	public TestBean(TestBean bean) {
		this.bean = bean;
		initList();
	}

	private void initList() {
		list.add("1");
		list.add("2");
		list.add("3");
		list.add(null);
	}

	public TestBean getBean() {
		return bean;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String encode(String data) {
		return "!" + data;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public List<String> getList() {
		return list;
	}
}

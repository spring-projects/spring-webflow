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
package org.springframework.faces.ui;

/**
 * Component that uses the Dojo implementation of Spring JavaScript to decorate a child input component with client-side
 * numeric validation behavior.
 * 
 * @author Jeremy Grelle
 * 
 */
public class DojoClientNumberValidator extends DojoDecoration {

	private static final String DOJO_COMPONENT_TYPE = "dijit.form.NumberTextBox";

	protected String[] getDojoAttributes() {
		return DojoDecoration.DOJO_ATTRS;
	}

	public String getDojoComponentType() {
		return DOJO_COMPONENT_TYPE;
	}

}

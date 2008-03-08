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
package org.springframework.faces.webflow;

import javax.el.CompositeELResolver;
import javax.faces.el.PropertyResolver;

import org.springframework.binding.expression.el.MapAdaptableELResolver;
import org.springframework.faces.expression.ELDelegatingPropertyResolver;

/**
 * For resolving MapAdaptable properties with JSF 1.1 or >.
 * 
 * @author Jeremy Grelle
 */
public class FlowPropertyResolver extends ELDelegatingPropertyResolver {

	private static final CompositeELResolver composite = new CompositeELResolver();

	static {
		composite.add(new MapAdaptableELResolver());
	}

	public FlowPropertyResolver(PropertyResolver nextResolver) {
		super(nextResolver, composite);
	}
}

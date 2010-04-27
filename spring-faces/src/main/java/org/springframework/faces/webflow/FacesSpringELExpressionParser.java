/*
 * Copyright 2004-2010 the original author or authors.
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

import org.springframework.binding.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

/**
 * <p>
 * A Spring EL {@link ExpressionParser} for use with JSF. Adds JSF specific Spring EL PropertyAccessors.
 * </p>
 * 
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class FacesSpringELExpressionParser extends WebFlowSpringELExpressionParser {

	public FacesSpringELExpressionParser(SpelExpressionParser expressionParser) {
		super(expressionParser);
		addPropertyAccessor(new JsfManagedBeanPropertyAccessor());
	}

}

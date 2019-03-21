/*
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.webflow.expression.spel;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.spel.SpringELExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * A sub-class for {@link SpringELExpressionParser} that registers Web Flow
 * specific Spring EL PropertyAccessors.
 *
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class WebFlowSpringELExpressionParser extends SpringELExpressionParser {


	public WebFlowSpringELExpressionParser(SpelExpressionParser expressionParser) {
		super(expressionParser);
		addDefaultPropertyAccessors();
	}

	public WebFlowSpringELExpressionParser(SpelExpressionParser parser, ConversionService conversionService) {
		super(parser, conversionService);
		addDefaultPropertyAccessors();
	}

	private void addDefaultPropertyAccessors() {
		addPropertyAccessor(new MessageSourcePropertyAccessor());
		addPropertyAccessor(new FlowVariablePropertyAccessor());
		addPropertyAccessor(new MapAdaptablePropertyAccessor());
		addPropertyAccessor(new ScopeSearchingPropertyAccessor());
		addPropertyAccessor(new BeanFactoryPropertyAccessor());
		addPropertyAccessor(new ActionPropertyAccessor());
	}

}

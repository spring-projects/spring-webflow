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
package org.springframework.binding.mapping;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.SettableExpression;

/**
 * A mapping that is required.
 * 
 * @author Keith Donald
 */
public class RequiredMapping extends Mapping {
	
	/**
	 * Creates a required mapping.
	 * @param sourceExpression the source mapping expression
	 * @param targetPropertyExpression the target property expression
	 * @param typeConverter a type converter
	 */
	public RequiredMapping(Expression sourceExpression, SettableExpression targetPropertyExpression,
			ConversionExecutor typeConverter) {
		super(sourceExpression, targetPropertyExpression, typeConverter, true);
	}
}

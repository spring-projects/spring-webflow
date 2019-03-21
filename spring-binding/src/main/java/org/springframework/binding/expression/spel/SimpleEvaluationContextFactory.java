/*
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.binding.expression.spel;

import java.util.List;

import org.springframework.binding.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.support.DataBindingPropertyAccessor;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

/**
 * Creates {@link SimpleEvaluationContext}, for use with data binding.
 *
 * @author Rossen Stoyanchev
 * @since 2.4.8
 */
public class SimpleEvaluationContextFactory implements EvaluationContextFactory {

	private static final PropertyAccessor dataBindingPropertyAccessor =
			DataBindingPropertyAccessor.forReadWriteAccess();


	private final List<PropertyAccessor> propertyAccessors;

	private final ConversionService conversionService;


	public SimpleEvaluationContextFactory(List<PropertyAccessor> propertyAccessors,
			ConversionService conversionService) {

		this.propertyAccessors = propertyAccessors;
		this.conversionService = conversionService;
	}


	@Override
	public EvaluationContext createContext(Object rootObject) {
		return SimpleEvaluationContext
				.forPropertyAccessors(getAccessorsArray())
				.withConversionService(conversionService.getDelegateConversionService())
				.withRootObject(rootObject)
				.build();
	}

	private PropertyAccessor[] getAccessorsArray() {
		int length = propertyAccessors.size() + 1;
		PropertyAccessor[] result = propertyAccessors.toArray(new PropertyAccessor[length]);
		result[length - 1] = dataBindingPropertyAccessor;
		return result;
	}

}

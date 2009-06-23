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
package org.springframework.binding.convert.service;

import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.util.Assert;

/**
 * A conversion executor that doesn't resolve its converter until its time to perform a conversion.
 * @author Keith Donald
 */
public class RuntimeBindingConversionExecutor implements ConversionExecutor {

	private Class targetClass;

	private ConversionService conversionService;

	/**
	 * Creates a new runtime binding conversion executor.
	 * @param targetClass the target type to convert to
	 * @param conversionService the conversion service to get converters from
	 */
	public RuntimeBindingConversionExecutor(Class targetClass, ConversionService conversionService) {
		Assert.notNull(targetClass, "The target class of the conversion is required");
		Assert.notNull(conversionService, "The conversion service is required");
		this.targetClass = targetClass;
		this.conversionService = conversionService;
	}

	public Class getSourceClass() {
		return null;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RuntimeBindingConversionExecutor)) {
			return false;
		}
		RuntimeBindingConversionExecutor o = (RuntimeBindingConversionExecutor) obj;
		return targetClass.equals(o.targetClass);
	}

	public int hashCode() {
		return targetClass.hashCode();
	}

	public Object execute(Object source) throws ConversionExecutionException {
		return execute(source, null);
	}

	public Object execute(Object source, Object context) throws ConversionExecutionException {
		return conversionService.getConversionExecutor(source.getClass(), targetClass).execute(source);
	}

}
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
import org.springframework.binding.convert.converters.Converter;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/**
 * A command object that is parameterized with the information necessary to perform a conversion of a source input to a
 * target output.
 * <p>
 * Specifically, encapsulates knowledge about how to convert source objects to a specific target type using a specific
 * converter.
 * 
 * @author Keith Donald
 */
public class StaticConversionExecutor implements ConversionExecutor {

	/**
	 * The source value type this executor will attempt to convert from.
	 */
	private final Class sourceClass;

	/**
	 * The target value type this executor will attempt to convert to.
	 */
	private final Class targetClass;

	/**
	 * The converter that will perform the conversion.
	 */
	private final Converter converter;

	/**
	 * Creates a conversion executor.
	 * @param sourceClass the source type that the converter will convert from
	 * @param targetClass the target type that the converter will convert to
	 * @param converter the converter that will perform the conversion
	 */
	public StaticConversionExecutor(Class sourceClass, Class targetClass, Converter converter) {
		Assert.notNull(sourceClass, "The source class is required");
		Assert.notNull(targetClass, "The target class is required");
		Assert.notNull(converter, "The converter is required");
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.converter = converter;
	}

	/**
	 * Returns the source class of conversions performed by this executor.
	 * @return the source class
	 */
	public Class getSourceClass() {
		return sourceClass;
	}

	/**
	 * Returns the target class of conversions performed by this executor.
	 * @return the target class
	 */
	public Class getTargetClass() {
		return targetClass;
	}

	/**
	 * Returns the converter that will perform the conversion.
	 * @return the converter
	 */
	public Converter getConverter() {
		return converter;
	}

	public Object execute(Object source) throws ConversionExecutionException {
		if (targetClass.isInstance(source)) {
			// source is already assignment compatible with target class
			return source;
		}
		if (source != null && !sourceClass.isInstance(source)) {
			throw new ConversionExecutionException(source, getSourceClass(), getTargetClass(), "Source object "
					+ source + " is expected to be an instance of " + getSourceClass());
		}
		try {
			return converter.convertSourceToTargetClass(source, targetClass);
		} catch (Exception e) {
			throw new ConversionExecutionException(source, getSourceClass(), getTargetClass(), e);
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof StaticConversionExecutor)) {
			return false;
		}
		StaticConversionExecutor other = (StaticConversionExecutor) o;
		return sourceClass.equals(other.sourceClass) && targetClass.equals(other.targetClass);
	}

	public int hashCode() {
		return sourceClass.hashCode() + targetClass.hashCode();
	}

	public String toString() {
		return new ToStringCreator(this).append("sourceClass", sourceClass).append("targetClass", targetClass)
				.toString();
	}
}
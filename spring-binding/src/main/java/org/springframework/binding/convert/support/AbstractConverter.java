/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.binding.convert.support;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.Converter;

/**
 * Base class for converters provided as a convenience to implementors.
 * 
 * @author Keith Donald
 */
public abstract class AbstractConverter implements Converter {

	/**
	 * Convenience convert method that converts the provided source to the first target object supported by this
	 * converter. Useful when a converter only supports conversion to a single target.
	 * @param source the source to convert
	 * @return the converted object
	 * @throws ConversionException an exception occured converting the source value
	 */
	public Object convert(Object source) throws ConversionException {
		return convert(source, getTargetClasses()[0], null);
	}

	/**
	 * Convenience convert method that converts the provided source to the target class specified with an empty
	 * conversion context.
	 * @param source the source to convert
	 * @param targetClass the target class to convert the source to, must be one of the supported
	 * <code>targetClasses</code>
	 * @return the converted object
	 * @throws ConversionException an exception occured converting the source value
	 */
	public Object convert(Object source, Class targetClass) throws ConversionException {
		return convert(source, targetClass, null);
	}

	/**
	 * Convenience convert method that converts the provided source to the first target object supported by this
	 * converter. Useful when a converter only supports conversion to a single target.
	 * @param source the source to convert
	 * @param context the conversion context, useful for influencing the behavior of the converter
	 * @return the converted object
	 * @throws ConversionException an exception occured converting the source value
	 */
	public Object convert(Object source, ConversionContext context) throws ConversionException {
		return convert(source, getTargetClasses()[0], context);
	}

	public Object convert(Object source, Class targetClass, ConversionContext context) throws ConversionException {
		try {
			return doConvert(source, targetClass, context);
		} catch (ConversionException e) {
			throw e;
		} catch (Throwable e) {
			// wrap in a ConversionException
			if (targetClass == null) {
				targetClass = getTargetClasses()[0];
			}
			throw new ConversionException(source, targetClass, e);
		}
	}

	/**
	 * Template method subclasses should override to actually perform the type conversion.
	 * @param source the source to convert from
	 * @param targetClass the target type to convert to
	 * @param context an optional conversion context that may be used to influence the conversion process, could be null
	 * @return the converted source value
	 * @throws Exception an exception occured, will be wrapped in a conversion exception if necessary
	 */
	protected abstract Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception;

}
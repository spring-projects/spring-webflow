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
package org.springframework.binding.convert;

/**
 * A type converter converts objects from one type to another. They may support conversion of multiple source types to
 * multiple target types.
 * <p>
 * Implementations of this interface are thread-safe.
 * 
 * @author Keith Donald
 */
public interface Converter {

	/**
	 * The source classes this converter can convert from.
	 * @return the supported source classes
	 */
	public Class[] getSourceClasses();

	/**
	 * The target classes this converter can convert to.
	 * @return the supported target classes
	 */
	public Class[] getTargetClasses();

	/**
	 * Convert the provided source object argument to an instance of the specified target class.
	 * @param source the source object to convert, its class must be one of the supported <code>sourceClasses</code>
	 * @param targetClass the target class to convert the source to, must be one of the supported
	 * <code>targetClasses</code>
	 * @param context an optional conversion context that may be used to influence the conversion process
	 * @return the converted object, an instance of the target type
	 * @throws ConversionException an exception occured during the conversion
	 */
	public Object convert(Object source, Class targetClass, ConversionContext context) throws ConversionException;

}
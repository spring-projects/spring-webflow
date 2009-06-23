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
package org.springframework.binding.convert.converters;

/**
 * A converter is capable of converting a source object of type {@link #getSourceClass()} to a target type of type
 * {@link #getTargetClass()}. If the converter is a {@link TwoWayConverter}, it can also convert from the target back
 * to the source.
 * <p>
 * Implementations of this interface are thread-safe and can be shared.
 * </p>
 * @author Keith Donald
 */
public interface Converter {

	/**
	 * The source class this converter can convert from. May be an interface or abstract type to allow this converter to
	 * convert specific subclasses as well.
	 * @return the source type
	 */
	public Class getSourceClass();

	/**
	 * The target class this converter can convert to. May be an interface or abstract type to allow this converter to
	 * convert specific subclasses as well.
	 * @return the target type
	 */
	public Class getTargetClass();

	/**
	 * Convert the provided source object argument to an instance of the specified target class.
	 * @param source the source object to convert, which must be an instance of {@link #getSourceClass()}
	 * @param targetClass the target class to convert the source to, which must be equal to or a specialization of
	 * {@link #getTargetClass()}
	 * @return the converted object, which must be an instance of the <code>targetClass</code>
	 * @throws Exception an exception occurred performing the conversion
	 */
	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception;

}
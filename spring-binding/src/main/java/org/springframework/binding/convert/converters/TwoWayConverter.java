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
 * A converter that can also convert from the target back to the source.
 * 
 * @author Keith Donald
 */
public interface TwoWayConverter extends Converter {

	/**
	 * Convert the provided target object argument to an instance of the specified source class.
	 * @param target the target object to convert, which must be an instance of {@link #getTargetClass()}
	 * @param sourceClass the source class to convert the target to, which must be equal to or a specialization of
	 * {@link #getSourceClass()}
	 * @return the converted object, which must be an instance of the <code>sourceClass</code>
	 * @throws Exception an exception occurred performing the conversion
	 */
	public Object convertTargetToSourceClass(Object target, Class sourceClass) throws Exception;

}
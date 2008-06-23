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
package org.springframework.binding.format;

/**
 * A Source for shared and commonly used <code>Formatters</code>.
 * 
 * @author Keith Donald
 */
public interface FormatterRegistry {

	/**
	 * Returns the default formatter installed for the given class of object.
	 * @param clazz the type of object that will be formatted
	 * @return the formatter
	 */
	public Formatter getFormatter(Class clazz);

	/**
	 * Returns the formatter for the given class of object with the given id. Use this method to query a custom
	 * formatter instance for a given class of object.
	 * @param clazz the type of object that will be formatted
	 * @param id the id of the custom formatter instance; typically descriptive like "localDate"
	 * @return the formatter
	 */
	public Formatter getFormatter(Class clazz, String id);

}
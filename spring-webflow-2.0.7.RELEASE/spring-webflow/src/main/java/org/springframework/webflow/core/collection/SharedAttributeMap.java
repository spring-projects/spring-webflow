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
package org.springframework.webflow.core.collection;

/**
 * An interface to be implemented by mutable attribute maps accessed by multiple threads that need to be synchronized.
 * 
 * @author Keith Donald
 */
public interface SharedAttributeMap extends MutableAttributeMap {

	/**
	 * Returns the shared map's mutex, which may be synchronized on to block access to the map by other threads.
	 */
	public Object getMutex();
}
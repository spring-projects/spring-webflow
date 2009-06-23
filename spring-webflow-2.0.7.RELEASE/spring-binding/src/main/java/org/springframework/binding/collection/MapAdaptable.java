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
package org.springframework.binding.collection;

import java.util.Map;

/**
 * An object whose contents are capable of being exposed as an unmodifiable map.
 * 
 * @author Keith Donald
 */
public interface MapAdaptable {

	/**
	 * Returns this object's contents as a {@link Map}. The returned map may or may not be modifiable depending on this
	 * implementation.
	 * <p>
	 * Warning: this operation may be called frequently; if so care should be taken so that the map contents (if
	 * calculated) be cached as appropriate.
	 * @return the object's contents as a map
	 */
	public Map asMap();

}
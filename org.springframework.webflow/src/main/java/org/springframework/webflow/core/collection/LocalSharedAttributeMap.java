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

import org.springframework.binding.collection.SharedMap;

/**
 * An attribute map that exposes a mutex that application code can synchronize on. This class wraps another shared map
 * in an attribute map.
 * <p>
 * The mutex can be used to serialize concurrent access to the shared map's contents by multiple threads.
 * 
 * @author Keith Donald
 */
public class LocalSharedAttributeMap extends LocalAttributeMap implements SharedAttributeMap {

	/**
	 * Creates a new shared attribute map.
	 * @param sharedMap the shared map
	 */
	public LocalSharedAttributeMap(SharedMap sharedMap) {
		super(sharedMap);
	}

	public Object getMutex() {
		return getSharedMap().getMutex();
	}

	/**
	 * Returns the wrapped shared map.
	 */
	protected SharedMap getSharedMap() {
		return (SharedMap) getMapInternal();
	}
}
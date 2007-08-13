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

import org.springframework.binding.convert.ConversionService;

/**
 * Marker interface that denotes an object has a dependency on a conversion service that is expected to be fulfilled.
 * 
 * @author Keith Donald
 */
public interface ConversionServiceAware {

	/**
	 * Set the conversion service this object should be made aware of (as it presumably depends on it).
	 * 
	 * @param conversionService the conversion service
	 */
	public void setConversionService(ConversionService conversionService);
}
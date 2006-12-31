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
package org.springframework.webflow.executor.jsf;

/**
 * Interface to be implemented by objects that can map Web Flow view names to
 * JSF view identifiers.
 * 
 * @author Colin Sampaleanu
 */
public interface ViewIdMapper {

	/**
	 * Map the given Web Flow view name to a JSF view id.
	 * @param viewName name of the view to map
	 * @return the corresponding JSF view id
	 */
	public String mapViewId(String viewName);

}
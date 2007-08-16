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
package org.springframework.faces.webflow;

import javax.faces.application.ViewHandler;

/**
 * Interface to be implemented by objects that can map Web Flow view names to JSF view identifiers. JSF view identifiers
 * are used to determine if the current view has changed and to create views by delegating to the application's
 * {@link ViewHandler}.
 * 
 * A view handler typically treats a JSF view id as the physical location of a view template encapsulating a page
 * layout. The JSF view id normally specifies the physical location of the view template minus a suffix. View handlers
 * typically replace the suffix of any view id with their own default suffix (e.g. ".jsp" or ".xhtml") and then try to
 * locate a physical template view.
 * 
 * @author Colin Sampaleanu
 */
public interface ViewIdMapper {

	/**
	 * Map the given Spring Web Flow view name to a JSF view identifier.
	 * @param viewName name of the view to map
	 * @return the corresponding JSF view id
	 */
	public String mapViewId(String viewName);

}
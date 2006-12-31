/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.execution;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Abstract base class for value objects that provide callers into a flow
 * execution information about a logical response to issue and the data
 * necessary to issue it.
 * <p>
 * This class is a generic marker returned when a request into an executing flow
 * has completed processing, indicating a client response needs to be issued. An
 * instance of a ViewSelection subclass represents the selection of a concrete
 * response type. It is expected that callers introspect the returned view
 * selection instance to handle the response types they support.
 * 
 * @see FlowExecution
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public abstract class ViewSelection implements Serializable {

	/**
	 * Constant for a <code>null</code> or empty view selection, indicating no
	 * response should be issued.
	 */
	public static final ViewSelection NULL_VIEW = new NullView();

	/**
	 * The definition of the 'null' view selection type, indicating that no
	 * response should be issued.
	 */
	private static final class NullView extends ViewSelection {

		// resolve the singleton instance
		private Object readResolve() throws ObjectStreamException {
			return NULL_VIEW;
		}

		public String toString() {
			return "null";
		}
	}
}
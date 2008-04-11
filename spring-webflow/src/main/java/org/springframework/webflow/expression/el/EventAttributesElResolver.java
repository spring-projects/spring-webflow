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
package org.springframework.webflow.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.springframework.webflow.execution.Event;

/**
 * Resolves attributes on an {@link Event} instance.
 * @author Keith Donald
 */
public class EventAttributesElResolver extends ELResolver {

	public Class getCommonPropertyType(ELContext elContext, Object base) {
		if (base instanceof Event) {
			return Object.class;
		} else {
			return null;
		}
	}

	public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class getType(ELContext elContext, Object base, Object property) {
		if (base instanceof Event) {
			elContext.setPropertyResolved(true);
			return Object.class;
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		if (base instanceof Event) {
			Event event = (Event) base;
			elContext.setPropertyResolved(true);
			return event.getAttributes().get(property.toString());
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		if (base instanceof Event) {
			elContext.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		if (base instanceof Event) {
			elContext.setPropertyResolved(true);
			throw new PropertyNotWritableException("Event attributes cannot be set with an expression.");
		}
	}
}
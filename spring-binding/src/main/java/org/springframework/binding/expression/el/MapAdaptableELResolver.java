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
package org.springframework.binding.expression.el;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.MapELResolver;

import org.springframework.binding.collection.MapAdaptable;

/**
 * An {@link ELResolver} for properly resolving variables in an instance of {@link MapAdaptable}
 * @author Jeremy Grelle
 */
public class MapAdaptableELResolver extends MapELResolver {

	public Class getType(ELContext context, Object base, Object property) {
		if (base instanceof MapAdaptable) {
			return super.getType(context, adapt(base), property);
		} else {
			return null;
		}
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base instanceof MapAdaptable) {
			return super.getValue(context, adapt(base), property);
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base instanceof MapAdaptable) {
			return super.isReadOnly(context, adapt(base), property);
		} else {
			return false;
		}
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base instanceof MapAdaptable) {
			super.setValue(context, adapt(base), property, value);
		}
	}

	private Map adapt(Object base) {
		MapAdaptable adaptable = (MapAdaptable) base;
		return adaptable.asMap();
	}

}

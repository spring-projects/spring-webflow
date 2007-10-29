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

import java.util.Iterator;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

/**
 * This {@link LifecycleFactory} implementation is responsible for installing a custom JSF Lifecycle the executes within
 * the context of Spring Web Flow.
 * <p>
 * The {@link FlowLifecycle} will be configured to delegate to the default {@link Lifecycle} where custom behavior is
 * not needed.
 * 
 * @author Jeremy Grelle
 */
public class FlowLifecycleFactory extends LifecycleFactory {

	public static final String FLOW_LIFECYCLE_ID = "org.springframework.FlowLifecycle";

	private final LifecycleFactory delegate;

	public FlowLifecycleFactory(LifecycleFactory delegate) {
		this.delegate = delegate;
		addLifecycle(FLOW_LIFECYCLE_ID, new FlowLifecycle(delegate.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE)));
	}

	public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
		delegate.addLifecycle(lifecycleId, lifecycle);
	}

	public Lifecycle getLifecycle(String lifecycleId) {
		return delegate.getLifecycle(lifecycleId);
	}

	public Iterator<String> getLifecycleIds() {
		return delegate.getLifecycleIds();
	}

}

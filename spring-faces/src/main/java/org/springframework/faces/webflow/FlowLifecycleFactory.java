package org.springframework.faces.webflow;

import java.util.Iterator;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

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

	public Iterator getLifecycleIds() {
		return delegate.getLifecycleIds();
	}

}

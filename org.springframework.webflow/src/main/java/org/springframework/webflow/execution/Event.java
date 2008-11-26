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
package org.springframework.webflow.execution;

import java.util.EventObject;

import org.springframework.util.Assert;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * Signals the occurrence of something an active flow execution should respond to. Each event has a string id that
 * provides a key for identifying what happened: e.g "coinInserted", or "pinDropped". Events may have attributes that
 * provide arbitrary payload data, e.g. "coin.amount=25", or "pinDropSpeed=25ms".
 * <p>
 * As an example, a "submit" event might signal that a Submit button was pressed in a web browser. A "success" event
 * might signal an action executed successfully. A "finish" event might signal a subflow ended normally.
 * <p>
 * Why is this not an interface? A specific design choice. An event is not a strategy that defines a generic type or
 * role--it is essentially an immutable value object. It is expected that specializations of this base class be "Events"
 * and not part of some other inheritance hierarchy.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Colin Sampaleanu
 */
public class Event extends EventObject {

	/**
	 * The event identifier.
	 */
	private final String id;

	/**
	 * The time the event occurred.
	 */
	private final long timestamp = System.currentTimeMillis();

	/**
	 * Additional event attributes that form this event's payload.
	 */
	private final AttributeMap attributes;

	/**
	 * Create a new event with the specified <code>id</code> and no payload.
	 * @param source the source of the event (required)
	 * @param id the event identifier (required)
	 */
	public Event(Object source, String id) {
		this(source, id, null);
	}

	/**
	 * Create a new event with the specified <code>id</code> and payload attributes.
	 * @param source the source of the event (required)
	 * @param id the event identifier (required)
	 * @param attributes additional event attributes
	 */
	public Event(Object source, String id, AttributeMap attributes) {
		super(source);
		Assert.hasText(id, "The event id is required: please set this event's id to a non-blank string identifier");
		this.id = id;
		this.attributes = attributes != null ? attributes : CollectionUtils.EMPTY_ATTRIBUTE_MAP;
	}

	/**
	 * Returns the event identifier.
	 * @return the event id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the time at which the event occurred, represented as the number of milliseconds since January 1, 1970,
	 * 00:00:00 GMT.
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns an unmodifiable map storing the attributes of this event. Never returns <code>null</code>.
	 * @return the event attributes (payload)
	 */
	public AttributeMap getAttributes() {
		return attributes;
	}

	public String toString() {
		return getId();
	}
}
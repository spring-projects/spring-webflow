package org.springframework.webflow.engine;

import org.springframework.core.enums.StaticLabeledEnum;

public class History extends StaticLabeledEnum {

	/**
	 * The history of the view state should be preserved when the view state exits to support back-tracking.
	 */
	public static final History PRESERVE = new History(0, "preserve");

	/**
	 * The history of the view state should be discarded when the view state exits to prevent back-tracking.
	 */
	public static final History DISCARD = new History(1, "discard");

	/**
	 * The history of the view state and all previous view state should be invalidated to completely restrict back
	 * tracking.
	 */
	public static final History INVALIDATE = new History(2, "invalidate");

	public History(int code, String label) {
		super(code, label);
	}

}

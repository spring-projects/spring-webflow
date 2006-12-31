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
package org.springframework.binding.format;

import java.text.DateFormat;

import org.springframework.core.enums.StaticLabeledEnum;

/**
 * Format styles, similar to those defined by {@link java.text.DateFormat}.
 * 
 * @author Keith Donald
 */
public class Style extends StaticLabeledEnum {

	/**
	 * See {@link java.text.DateFormat#FULL}.
	 */
	public static final Style FULL = new Style(DateFormat.FULL, "Full");

	/**
	 * See {@link java.text.DateFormat#LONG}.
	 */
	public static final Style LONG = new Style(DateFormat.LONG, "Long");

	/**
	 * See {@link java.text.DateFormat#MEDIUM}.
	 */
	public static final Style MEDIUM = new Style(DateFormat.MEDIUM, "Medium");

	/**
	 * See {@link java.text.DateFormat#SHORT}.
	 */
	public static final Style SHORT = new Style(DateFormat.SHORT, "Short");

	/**
	 * Private constructor since this is a type-safe enum.
	 */
	private Style(int code, String label) {
		super(code, label);
	}
}
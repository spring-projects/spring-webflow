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
package org.springframework.binding.message;

import org.springframework.core.enums.StaticLabeledEnum;

/**
 * Enum exposing supported message severities.
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 * @see Message
 */
public class Severity extends StaticLabeledEnum {

	/**
	 * The "Informational" severity. Used to indicate a successful operation or result.
	 */
	public static final Severity INFO = new Severity(0, "Info");

	/**
	 * The "Warning" severity. Used to indicate there is a minor problem, or to inform the message receiver of possible
	 * misuse, or to indicate a problem may arise in the future.
	 */
	public static final Severity WARNING = new Severity(1, "Warning");

	/**
	 * The "Error" severity. Used to indicate a significant problem like a business rule violation.
	 */
	public static final Severity ERROR = new Severity(2, "Error");

	/**
	 * The "Fatal" severity. Used to indicate a fatal problem like a system error.
	 */
	public static final Severity FATAL = new Severity(3, "Fatal");

	private Severity(int code, String label) {
		super(code, label);
	}
}

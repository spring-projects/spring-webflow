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
package org.springframework.webflow.execution.repository;

/**
 * Thrown when an encoded flow execution key is badly formatted and could not be parsed.
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
public class BadlyFormattedFlowExecutionKeyException extends FlowExecutionRepositoryException {

	/**
	 * The string encoded flow execution key that was invalid.
	 */
	private String invalidKey;

	/**
	 * The format the string key should have been in. Could just be a description of that format.
	 */
	private String format;

	/**
	 * Creates a bad execution key format exception.
	 * @param invalidKey the invalid key
	 * @param format the format the key should have been in
	 */
	public BadlyFormattedFlowExecutionKeyException(String invalidKey, String format) {
		super("Badly formatted flow execution key '" + invalidKey + "', the expected format is '" + format + "'");
		this.invalidKey = invalidKey;
		this.format = format;
	}

	/**
	 * Creates a bad execution key format exception.
	 * @param invalidKey the invalid key
	 * @param format the format the key should have been in
	 * @param cause the cause
	 */
	public BadlyFormattedFlowExecutionKeyException(String invalidKey, String format, Throwable cause) {
		super("Badly formatted flow execution key '" + invalidKey + "', the expected format is '" + format + "'", cause);
		this.invalidKey = invalidKey;
		this.format = format;
	}

	/**
	 * Returns the string key of the flow execution that could not be parsed.
	 */
	public String getInvalidKey() {
		return invalidKey;
	}

	/**
	 * Returns the format the key should have been in.
	 */
	public String getFormat() {
		return format;
	}
}
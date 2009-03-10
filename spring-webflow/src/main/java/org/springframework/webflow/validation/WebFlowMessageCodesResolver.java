package org.springframework.webflow.validation;

import org.springframework.validation.MessageCodesResolver;

public class WebFlowMessageCodesResolver implements MessageCodesResolver {

	private String failureMessageCodePrefix = "";

	/**
	 * The prefix to prepend to all validation failure message codes.
	 */
	public String getFailureMessageCodePrefix() {
		return failureMessageCodePrefix;
	}

	/**
	 * A prefix to prepend to all validation failure message codes; default if not set explicitly is "validation".
	 * @param failureMessageCodePrefix the failure message code prefix
	 */
	public void setFailureMessageCodePrefix(String failureMessageCodePrefix) {
		this.failureMessageCodePrefix = failureMessageCodePrefix;
	}

	public String[] resolveMessageCodes(String errorCode, String objectName) {
		String constraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(errorCode)
				.toString();
		String objectConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator())
				.append(objectName).append(codeSeparator()).append(errorCode).toString();
		return new String[] { objectConstraintMessageCode, constraintMessageCode };
	}

	public String[] resolveMessageCodes(String errorCode, String objectName, String field, Class fieldType) {
		String propertyConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
				objectName).append(codeSeparator()).append(field).append(codeSeparator()).append(errorCode).toString();
		String typeConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(fieldType)
				.append(codeSeparator()).append(errorCode).toString();
		return new String[] { propertyConstraintMessageCode, typeConstraintMessageCode, propertyConstraintMessageCode };
	}

	protected StringBuilder appendFailureMessageCodePrefix() {
		return new StringBuilder().append(failureMessageCodePrefix);
	}

	protected char codeSeparator() {
		return '.';
	}

}

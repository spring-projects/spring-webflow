package org.springframework.binding.validation;

/**
 * A helper factory for validation failure message codes. Subclasses may override
 * {@link #createMessageCodes(ValidationFailure, ValidationFailureModelContext)} to customize how failure message codes
 * are translated.
 * 
 * For a property validation failure, the default algorithm in this class returns these message codes:
 * 
 * <pre>
 * ${failureMessageCodePrefix}.${model}.${property}.${constraint}
 * ${failureMessageCodePrefix}.${propertyType}.${constraint}
 * ${failureMessageCodePrefix}.${constraint}
 * </pre>
 * 
 * @author Keith Donald
 */
public class ValidationFailureMessageCodesFactory {

	private String failureMessageCodePrefix = "validation";

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

	public String[] createMessageCodes(ValidationFailure failure, ValidationFailureModelContext modelContext) {
		String constraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
				failure.getConstraint()).toString();
		if (failure.getProperty() != null) {
			String propertyConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
					modelContext.getModel()).append(codeSeparator()).append(failure.getProperty()).append(
					codeSeparator()).append(failure.getConstraint()).toString();
			String typeConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
					modelContext.getPropertyType().getName()).append(codeSeparator()).append(failure.getConstraint())
					.toString();
			return new String[] { propertyConstraintMessageCode, typeConstraintMessageCode, constraintMessageCode };
		} else {
			String objectConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
					modelContext.getModel()).append(codeSeparator()).append(failure.getConstraint()).toString();
			return new String[] { objectConstraintMessageCode, constraintMessageCode };
		}
	}

	protected StringBuilder appendFailureMessageCodePrefix() {
		return new StringBuilder().append(failureMessageCodePrefix);
	}

	protected char codeSeparator() {
		return DefaultValidationFailureMessageResolverFactory.CODE_SEPARATOR;
	}
}
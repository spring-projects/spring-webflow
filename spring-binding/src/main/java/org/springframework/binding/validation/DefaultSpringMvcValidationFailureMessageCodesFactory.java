package org.springframework.binding.validation;

/**
 * Failure message codes factory specialization that applies default Spring MVC errorCode-to-messageCodes mapping rules.
 * The difference between this subclass and the default {@link ValidationFailureMessageCodesFactory} is where the
 * constraint code falls in the message code. Specifically, for a property validation failure the default algorithm
 * results in these codes:
 * 
 * <pre>
 * ${failureMessageCodePrefix}.${model}.${property}.${constraint}
 * ${failureMessageCodePrefix}.${propertyType}.${constraint}
 * ${failureMessageCodePrefix}.${constraint}
 * </pre>
 * 
 * while this original Spring MVC algorithm results in these codes:
 * 
 * <pre>
 * ${failureMessageCodePrefix}.${constraint}.${model}.${property}
 * ${failureMessageCodePrefix}.${constraint}.${propertyType}
 * ${failureMessageCodePrefix}.${constraint}
 * </pre>
 * 
 * @author Keith Donald
 */
public class DefaultSpringMvcValidationFailureMessageCodesFactory extends ValidationFailureMessageCodesFactory {

	public String[] createMessageCodes(ValidationFailure failure, ValidationFailureModelContext modelContext) {
		String constraintMessageCode = appendFailureMessageCodePrefix().append(failure.getConstraint()).append(
				codeSeparator()).toString();
		if (failure.getProperty() != null) {
			String propertyConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
					failure.getConstraint()).append(modelContext.getModel()).append(codeSeparator()).append(
					failure.getProperty()).append(codeSeparator()).toString();
			String typeConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
					failure.getConstraint()).append(modelContext.getPropertyType().getName()).toString();
			return new String[] { propertyConstraintMessageCode, typeConstraintMessageCode, constraintMessageCode };
		} else {
			String objectConstraintMessageCode = appendFailureMessageCodePrefix().append(codeSeparator()).append(
					failure.getConstraint()).append(modelContext.getModel()).append(codeSeparator()).toString();
			return new String[] { objectConstraintMessageCode, constraintMessageCode };
		}
	}

}
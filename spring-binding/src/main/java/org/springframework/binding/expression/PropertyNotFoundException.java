package org.springframework.binding.expression;

/**
 * An evaluation exception indicating a expression that references a property failed to evaluate because the property
 * could not be found.
 * @author Keith Donald
 */
public class PropertyNotFoundException extends EvaluationException {

	/**
	 * Creates a new property not found exception
	 * @param evaluationAttempt the evaluaion attempt details
	 * @param cause root cause of the failure
	 */
	public PropertyNotFoundException(EvaluationAttempt evaluationAttempt, Throwable cause) {
		super(evaluationAttempt, cause);
	}
}

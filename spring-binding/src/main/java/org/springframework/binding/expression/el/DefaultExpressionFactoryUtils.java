package org.springframework.binding.expression.el;

import javax.el.ExpressionFactory;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

/**
 * A helper for creating a new expression factory instance using the default expression factory class configured for the
 * VM.
 * 
 * @author Keith Donald
 */
public class DefaultExpressionFactoryUtils {

	// TODO - change default to Spring EL when it becomes available
	private static final String DEFAULT_EXPRESSION_FACTORY = "org.jboss.el.ExpressionFactoryImpl";

	/**
	 * Returns the type of ExpressionFactory configured for this VM.
	 */
	public static String getDefaultExpressionFactoryClassName() {
		return DEFAULT_EXPRESSION_FACTORY;
	}

	/**
	 * Creates a new instance of the expression factory configured for this VM.
	 * @throws IllegalStateException if the ExpressionFactory class cannot be found
	 * @throws RuntimeException if the ExpressionFactory cannot be instantiated
	 */
	public static ExpressionFactory createExpressionFactory() throws IllegalStateException, RuntimeException {
		// Fallback in the case of using an older version of the EL API
		try {
			Class expressionFactoryClass = ClassUtils.forName(getDefaultExpressionFactoryClassName());
			return (ExpressionFactory) BeanUtils.instantiateClass(expressionFactoryClass);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(
					"The default ExpressionFactory class '"
							+ getDefaultExpressionFactoryClassName()
							+ "' could not be found in the classpath.  "
							+ "Please add this to your classpath or set the default ExpressionFactory class name to something that is in the classpath.");
		} catch (NoClassDefFoundError e) {
			throw new IllegalStateException(
					"The default ExpressionFactory class '"
							+ getDefaultExpressionFactoryClassName()
							+ "' could not be found in the classpath.  "
							+ "Please add this to your classpath or set the default ExpressionFactory class name to something that is in the classpath.");
		} catch (BeanInstantiationException e) {
			throw new RuntimeException("An instance of the default ExpressionFactory '"
					+ getDefaultExpressionFactoryClassName()
					+ "' could not be instantiated.  Check your EL implementation configuration.", e);
		}
	}
}
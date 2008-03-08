package org.springframework.binding.expression.el;

import javax.el.ExpressionFactory;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * A helper for creating a new expression factory instance using the default expression factory class configured for the
 * VM.
 * 
 * @author Keith Donald
 */
public class DefaultExpressionFactoryUtils {
	private static final String EXPRESSION_FACTORY_PROPERTY = "javax.el.ExpressionFactory";

	static {
		if (!System.getProperties().containsKey(EXPRESSION_FACTORY_PROPERTY)) {
			// TODO - change default to Spring EL when it becomes available
			setDefaultExpressionFactoryClassName("org.jboss.el.ExpressionFactoryImpl");
		}
	}

	/**
	 * Returns the type of ExpressionFactory configured for this VM.
	 */
	public static String getDefaultExpressionFactoryClassName() {
		return System.getProperty(EXPRESSION_FACTORY_PROPERTY);
	}

	/**
	 * Sets the type of ExpressionFactory configured for this VM.
	 */
	public static void setDefaultExpressionFactoryClassName(String expressionFactoryClassName) {
		System.setProperty(EXPRESSION_FACTORY_PROPERTY, expressionFactoryClassName);
	}

	/**
	 * Creates a new instance of the expression factory configured for this VM.
	 * @throws IllegalStateException if the ExpressionFactory class cannot be found
	 * @throws RuntimeException if the ExpressionFactory cannot be instantiated
	 */
	public static ExpressionFactory createExpressionFactory() throws IllegalStateException, RuntimeException {
		if (ReflectionUtils.findMethod(ExpressionFactory.class, "newInstance") != null) {
			return ExpressionFactory.newInstance();
		} else {
			// Fallback in the case of using an older version of the EL API
			try {
				Class expressionFactoryClass = ClassUtils.forName(getDefaultExpressionFactoryClassName());
				return (ExpressionFactory) BeanUtils.instantiateClass(expressionFactoryClass);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(
						"The default ExpressionFactory class '"
								+ getDefaultExpressionFactoryClassName()
								+ "' could not be found in the classpath.  "
								+ "Please add this to your classpath or set the default ExpressionFactory class name to something that is in the classpath.",
						e);
			} catch (NoClassDefFoundError e) {
				throw new IllegalStateException(
						"The default ExpressionFactory class '"
								+ getDefaultExpressionFactoryClassName()
								+ "' could not be found in the classpath.  "
								+ "Please add this to your classpath or set the default ExpressionFactory class name to something that is in the classpath.",
						e);
			} catch (BeanInstantiationException e) {
				throw new RuntimeException("An instance of the default ExpressionFactory '"
						+ getDefaultExpressionFactoryClassName()
						+ "' could not be instantiated.  Check your EL implementation configuration.", e);
			}
		}
	}
}
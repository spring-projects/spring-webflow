package org.springframework.webflow.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

/**
 * Message Codes Resolver that implements the default Web Flow 2.x algorithm. The default algorithm differs from the
 * Spring MVC {@link DefaultMessageCodesResolver} by appending the errorCode <em>last</em> instead of first. For
 * example: myBean.myProperty.required instead of required.myBean.myProperty.
 * 
 * Specifically:
 * <p>
 * Will create two message codes for an object error, in the following order:
 * <ul>
 * <li>1.: objectName.errorCode
 * <li>2.: errorCode
 * </ul>
 * 
 * <p>
 * Will create four message codes for a field error, in the following order:
 * <ul>
 * <li>1.: object name.field.rrorCode
 * <li>2.: field.errorCode
 * <li>3.: fieldType.errorCode
 * <li>4.: errorCode
 * </ul>
 * 
 * <p>
 * For example, in case of code "typeMismatch", object name "user", field "age" of type Integer:
 * <ul>
 * <li>1. try "user.age.typeMismatch"
 * <li>2. try "age.typeMismatch"
 * <li>3. try "java.lang.Integer.typeMismatch"
 * <li>4. try "typeMismatch"
 * </ul>
 * 
 * @author Keith Donald
 */
public class WebFlowMessageCodesResolver implements MessageCodesResolver {

	/**
	 * The separator that this implementation uses when resolving message codes.
	 */
	public static final String CODE_SEPARATOR = ".";

	private String prefix = "";

	/**
	 * Specify a prefix to be applied to any code built by this resolver.
	 * <p>
	 * Default is none. Specify, for example, "validation." to get error codes like "validation.name.typeMismatch".
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Return the prefix to be applied to any code built by this resolver.
	 * <p>
	 * Returns an empty String in case of no prefix.
	 */
	protected String getPrefix() {
		return this.prefix;
	}

	public String[] resolveMessageCodes(String errorCode, String objectName) {
		return new String[] { postProcessMessageCode(objectName + CODE_SEPARATOR + errorCode),
				postProcessMessageCode(errorCode) };
	}

	/**
	 * Build the code list for the given code and field: an object/field-specific code, a field-specific code, a plain
	 * error code.
	 * <p>
	 * Arrays, Lists and Maps are resolved both for specific elements and the whole collection.
	 * <p>
	 * See the {@link DefaultMessageCodesResolver class level Javadoc} for details on the generated codes.
	 * @return the list of codes
	 */
	public String[] resolveMessageCodes(String errorCode, String objectName, String field, Class<?> fieldType) {
		List<String> codeList = new ArrayList<String>();
		List<String> fieldList = new ArrayList<String>();
		buildFieldList(field, fieldList);
		for (String fieldInList : fieldList) {
			codeList.add(postProcessMessageCode(objectName + CODE_SEPARATOR + fieldInList + CODE_SEPARATOR + errorCode));
		}
		int dotIndex = field.lastIndexOf('.');
		if (dotIndex != -1) {
			buildFieldList(field.substring(dotIndex + 1), fieldList);
		}
		for (String fieldInList : fieldList) {
			codeList.add(postProcessMessageCode(fieldInList + CODE_SEPARATOR + errorCode));
		}
		if (fieldType != null) {
			codeList.add(postProcessMessageCode(fieldType.getName() + CODE_SEPARATOR + errorCode));
		}
		codeList.add(postProcessMessageCode(errorCode));
		return StringUtils.toStringArray(codeList);
	}

	/**
	 * Add both keyed and non-keyed entries for the supplied <code>field</code> to the supplied field list.
	 */
	protected void buildFieldList(String field, List<String> fieldList) {
		fieldList.add(field);
		String plainField = field;
		int keyIndex = plainField.lastIndexOf('[');
		while (keyIndex != -1) {
			int endKeyIndex = plainField.indexOf(']', keyIndex);
			if (endKeyIndex != -1) {
				plainField = plainField.substring(0, keyIndex) + plainField.substring(endKeyIndex + 1);
				fieldList.add(plainField);
				keyIndex = plainField.lastIndexOf('[');
			} else {
				keyIndex = -1;
			}
		}
	}

	/**
	 * Post-process the given message code, built by this resolver.
	 * <p>
	 * The default implementation applies the specified prefix, if any.
	 * @param code the message code as built by this resolver
	 * @return the final message code to be returned
	 * @see #setPrefix
	 */
	protected String postProcessMessageCode(String code) {
		return getPrefix() + code;
	}

}

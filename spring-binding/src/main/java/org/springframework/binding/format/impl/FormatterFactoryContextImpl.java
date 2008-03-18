package org.springframework.binding.format.impl;

import java.util.Locale;

import org.springframework.binding.format.FormatterFactoryContext;

public class FormatterFactoryContextImpl implements FormatterFactoryContext {

	private Class formattedClass;

	private Locale locale;

	public Class getFormattedClass() {
		return formattedClass;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setFormattedClass(Class formattedClass) {
		this.formattedClass = formattedClass;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}

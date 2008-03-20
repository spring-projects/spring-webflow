package org.springframework.webflow.mvc;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public abstract class ViewRenderingErrors implements Errors {

	public void addAllErrors(Errors errors) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public int getErrorCount() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public FieldError getFieldError() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public FieldError getFieldError(String field) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public int getFieldErrorCount() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public int getFieldErrorCount(String field) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public List getFieldErrors() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public ObjectError getGlobalError() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public int getGlobalErrorCount() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public String getNestedPath() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public String getObjectName() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public boolean hasErrors() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public boolean hasFieldErrors() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public boolean hasFieldErrors(String field) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public boolean hasGlobalErrors() {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void popNestedPath() throws IllegalStateException {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void pushNestedPath(String path) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void reject(String errorCode, String defaultMessage) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void reject(String errorCode) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void rejectValue(String field, String errorCode, Object[] args, String defaultMessage) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void rejectValue(String field, String errorCode, String defaultMessage) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void rejectValue(String field, String errorCode) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

	public void setNestedPath(String path) {
		throw new UnsupportedOperationException("Not needed during view rendering");
	}

}

package org.springframework.binding.mapping;

public abstract class Result {
	public abstract Object getOriginalValue();

	public abstract Object getMappedValue();

	public abstract boolean isError();

	public abstract String getErrorCode();
}

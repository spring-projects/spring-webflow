package org.springframework.binding.expression.support;

public interface TestMethods {

	public void doSomethingWithInt(int arg);

	public String returnStringFromInt(int arg);

	public String returnStringFromIntAndObject(int arg, TestBean bean);
}

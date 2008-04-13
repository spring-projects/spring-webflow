/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELResolver;

/**
 * A factory for creating a EL context object that will be used to evaluate a target object of an EL expression.
 * 
 * Note this ELContextFactory is not used at parse time, only evaluation time. Therefore, factories should not be
 * concerned with setting up parse-time context attributes such as the variable mapper and function mapper that play no
 * part during expression evaluation.
 * 
 * @author Keith Donald
 * @author Jeremy Grelle
 */
public interface ELContextFactory {

	/**
	 * Configures and returns an {@link ELContext} to be used in evaluating EL expressions on the given base target
	 * object. In certain environments the target will be null and the base object of the expression is expected to be
	 * resolved via the ELContext's {@link ELResolver} chain.
	 * @param target The base object for the expression evaluation
	 * @return ELContext The configured ELContext instance for evaluating expressions.
	 */
	public ELContext getELContext(Object target);

}
/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Support for testing flows and their associated artifacts.
 *
 * <p>When you want to unit test one of your flows the
 * {@link org.springframework.webflow.test.execution.AbstractFlowExecutionTests} and its associated subclasses provide
 * a base you can extend.
 *
 * <p>When unit testing flow artifacts such as actions in isolation, the
 * {@link org.springframework.webflow.test.MockRequestContext} is of particular interest.
 *
 * <p>All mock implementations provided by this package are NOT intended to be used for anything but standalone unit
 * tests. They are simple state holders, <i>stub</i> implementations, at least if you follow
 * <a href="https://www.martinfowler.com/articles/mocksArentStubs.html">Martin Fowler's</a> reasoning. These classes
 * are called <i>Mock</i>s to be consistent with the naming convention in the rest of the Spring framework
 * (e.g. {@code MockHttpServletRequest}, ...).
 */
package org.springframework.webflow.test;


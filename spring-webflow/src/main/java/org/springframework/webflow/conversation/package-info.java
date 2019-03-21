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
 * The conversation subsystem for beginning and ending conversations that manage the state of user interactions.
 *
 * <p>The central concept defined by this package is the
 * {@link org.springframework.webflow.conversation.ConversationManager}, representing a service interface for
 * managing conversations.
 *
 * <p>This package serves as a portable conversation management abstraction and does not depend on the Spring Web
 * Flow engine. It is used by the flow execution repository subsystem to store conversation related state.
 */
package org.springframework.webflow.conversation;


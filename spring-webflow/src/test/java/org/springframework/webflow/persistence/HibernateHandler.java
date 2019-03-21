/*
 * Copyright 2004-2016 the original author or authors.
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
package org.springframework.webflow.persistence;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;

interface HibernateHandler {
	
	void templateSave(Object entity);

	<T> T templateGet(Class<T> entityClass, Serializable id);

	PlatformTransactionManager getTransactionManager();
	
	SessionFactory getSessionFactory();
	
	void templateExecuteWithNativeSession(SessionCallback callback);
	
	interface SessionCallback {

		void doWithSession(Session session);

	}

}

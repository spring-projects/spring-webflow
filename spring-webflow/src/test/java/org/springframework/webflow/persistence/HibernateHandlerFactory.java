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
import java.util.function.Function;

import javax.sql.DataSource;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class HibernateHandlerFactory {
	
	static HibernateHandler create(DataSource dataSource) throws Exception {
		return new Hibernate5Handler(dataSource);
	}
	
	private static class Hibernate5Handler implements HibernateHandler {

		private final PlatformTransactionManager transactionManager;
		
		private final SessionFactory sessionFactory;

		private Hibernate5Handler(DataSource dataSource) throws Exception {
			sessionFactory = getSessionFactory(dataSource);
			transactionManager = new HibernateTransactionManager(sessionFactory);
		}

		public void templateSave(Object entity) {
			doExecuteWithNativeSession(session -> {
				this.sessionFactory.getCurrentSession().persist(entity);
				return null;
			});
		}

		public <T> T templateGet(Class<T> entityClass, Serializable id) {
			return doExecuteWithNativeSession(session ->
					this.sessionFactory.getCurrentSession().find(entityClass, id));
		}

		public void templateExecuteWithNativeSession(final SessionCallback callback) {
			doExecuteWithNativeSession(session -> {
				callback.doWithSession(session);
				return null;
			});
		}

		private <T> T doExecuteWithNativeSession(Function<Session, T> callback) {
			Session session = getSessionFactory().getCurrentSession();
			boolean isNew = false;
			if (session == null) {
				session = getSessionFactory().openSession();
				session.setHibernateFlushMode(FlushMode.MANUAL);
				isNew = true;
			}
			try {
				return callback.apply(session);
			}
			finally {
				if (isNew) {
					session.close();
				}
			}
		}

		public PlatformTransactionManager getTransactionManager() {
			return transactionManager;
		}
		
		public SessionFactory getSessionFactory() {
			return sessionFactory;
		}

		private SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
			LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
			factory.setDataSource(dataSource);
			factory.setMappingLocations(
					new ClassPathResource("org/springframework/webflow/persistence/TestBean.hbm.xml"),
					new ClassPathResource("org/springframework/webflow/persistence/TestAddress.hbm.xml"));
			factory.afterPropertiesSet();
			return factory.getObject();
		}

	}

}

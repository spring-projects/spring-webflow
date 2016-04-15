/*
 * Copyright 2004-2016 the original author or authors.
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
package org.springframework.webflow.persistence;

import java.io.Serializable;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ClassUtils;

public class HibernateHandlerFactory {
	
	static HibernateHandler create(DataSource dataSource) throws Exception {
		if (ClassUtils.isPresent("org.hibernate.engine.transaction.spi.TransactionContext",
				HibernateHandlerFactory.class.getClassLoader())) {
			return new Hibernate4Handler(dataSource);
		}
		return new Hibernate5Handler(dataSource);
	}
	
	private static class Hibernate4Handler implements HibernateHandler {

		private final HibernateTemplate template;

		private final PlatformTransactionManager tranasactionManager;
		
		private final SessionFactory sessionFactory;

		private Hibernate4Handler(DataSource dataSource) throws Exception {
			sessionFactory = createSessionFactory(dataSource);
			template = new HibernateTemplate(sessionFactory);
			template.setCheckWriteOperations(false);
			tranasactionManager = new HibernateTransactionManager(sessionFactory);
		}

		public void templateSave(Object entity) {
			template.save(entity);
		}

		public <T> T templateGet(Class<T> entityClass, Serializable id) {
			return template.get(entityClass, id);
		}

		public void templateExecuteWithNativeSession(final SessionCallback callback) {
			template.executeWithNativeSession(new HibernateCallback<Void>() {

				@Override
				public Void doInHibernate(Session session) throws HibernateException {
					callback.doWithSession(session);
					return null;
				}
			});
		}

		public PlatformTransactionManager getTransactionManager() {
			return tranasactionManager;
		}
		
		public SessionFactory getSessionFactory() {
			return sessionFactory;
		}

		private static SessionFactory createSessionFactory(DataSource dataSource) throws Exception {
			LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
			factory.setDataSource(dataSource);
			factory.setMappingLocations(new Resource[] {
					new ClassPathResource("org/springframework/webflow/persistence/TestBean.hbm.xml"),
					new ClassPathResource("org/springframework/webflow/persistence/TestAddress.hbm.xml") });
			factory.afterPropertiesSet();
			return factory.getObject();
		}

	}

	private static class Hibernate5Handler implements HibernateHandler {

		private final org.springframework.orm.hibernate5.HibernateTemplate template;

		private final PlatformTransactionManager tranasactionManager;
		
		private final SessionFactory sessionFactory;

		private Hibernate5Handler(DataSource dataSource) throws Exception {
			sessionFactory = getSessionFactory(dataSource);
			template = new org.springframework.orm.hibernate5.HibernateTemplate(sessionFactory);
			template.setCheckWriteOperations(false);
			tranasactionManager = new org.springframework.orm.hibernate5.HibernateTransactionManager(sessionFactory);
		}

		public void templateSave(Object entity) {
			template.save(entity);
		}

		public <T> T templateGet(Class<T> entityClass, Serializable id) {
			return template.get(entityClass, id);
		}

		public void templateExecuteWithNativeSession(final SessionCallback callback) {
			template.executeWithNativeSession(new org.springframework.orm.hibernate5.HibernateCallback<Void>() {

				@Override
				public Void doInHibernate(Session session) throws HibernateException {
					callback.doWithSession(session);
					return null;
				}
			});
		}

		public PlatformTransactionManager getTransactionManager() {
			return tranasactionManager;
		}
		
		public SessionFactory getSessionFactory() {
			return sessionFactory;
		}

		private SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
			org.springframework.orm.hibernate5.LocalSessionFactoryBean factory = new org.springframework.orm.hibernate5.LocalSessionFactoryBean();
			factory.setDataSource(dataSource);
			factory.setMappingLocations(new Resource[] {
					new ClassPathResource("org/springframework/webflow/persistence/TestBean.hbm.xml"),
					new ClassPathResource("org/springframework/webflow/persistence/TestAddress.hbm.xml") });
			factory.afterPropertiesSet();
			return factory.getObject();
		}

	}

}

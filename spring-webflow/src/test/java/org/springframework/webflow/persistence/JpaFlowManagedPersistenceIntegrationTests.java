package org.springframework.webflow.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;

public class JpaFlowManagedPersistenceIntegrationTests extends AbstractFlowManagedPersistenceIntegrationTests {

	private EntityManagerFactory entityManagerFactory;

	@Override
	protected FlowExecutionListener createFlowExecutionListener() {
		entityManagerFactory = getEntityManagerFactory(getDataSource());
		JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory);
		return new JpaFlowExecutionListener(entityManagerFactory, tm);
	}

	@Override
	protected Action incrementCountAction() {
		return new Action() {
			public Event execute(RequestContext context) {
				assertSessionBound();
				EntityManager em = (EntityManager) context.getFlowScope().get("persistenceContext");
				TestBean bean = em.getReference(TestBean.class, 0L);
				bean.incrementCount();
				assertNotNull(bean);
				return new Event(this, "success");
			}
		};
	}

	@Override
	protected Object assertCountAction() {
		return new Object() {
			@SuppressWarnings("unused")
			public void execute(RequestContext context, int expected) {
				assertSessionBound();
				EntityManager em = (EntityManager) context.getFlowScope().get("persistenceContext");
				TestBean bean = em.getReference(TestBean.class, 0L);
				assertEquals(expected, bean.getCount());
			}
		};
	}

	@Override
	protected void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
	}

	/* private helper methods */

	private EntityManagerFactory getEntityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceXmlLocation("classpath:org/springframework/webflow/persistence/persistence.xml");
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

}

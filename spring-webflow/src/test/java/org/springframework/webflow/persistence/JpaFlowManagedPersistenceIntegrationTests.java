package org.springframework.webflow.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;

public class JpaFlowManagedPersistenceIntegrationTests extends AbstractFlowManagedPersistenceIntegrationTests {

	private EntityManagerFactory entityManagerFactory;

	@Override
	protected FlowExecutionListener createFlowExecutionListener() throws Exception {
		entityManagerFactory = getEntityManagerFactory(getDataSource());
		JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory);
		return new JpaFlowExecutionListener(entityManagerFactory, tm);
	}

	@Override
	protected Action incrementCountAction() {
		return new Action() {
			@SuppressWarnings("cast")
			public Event execute(RequestContext context) throws Exception {
				assertSessionBound();
				EntityManager em = (EntityManager) context.getFlowScope().get("persistenceContext");
				TestBean bean = (TestBean) em.getReference(TestBean.class, new Integer(0));
				bean.incrementCount();
				assertNotNull(bean);
				return new Event(this, "success");
			}
		};
	}

	@Override
	protected Object assertCountAction() {
		return new Object() {
			@SuppressWarnings({ "unused", "cast" })
			public void execute(RequestContext context, int expected) throws Exception {
				assertSessionBound();
				EntityManager em = (EntityManager) context.getFlowScope().get("persistenceContext");
				TestBean bean = (TestBean) em.getReference(TestBean.class, new Integer(0));
				assertEquals(expected, bean.getCount());
			}
		};
	}

	@Override
	protected void assertSessionBound() {
		assertNotNull(TransactionSynchronizationManager.getResource(entityManagerFactory));
	}

	/* private helper methods */

	private EntityManagerFactory getEntityManagerFactory(DataSource dataSource) throws Exception {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPersistenceXmlLocation("classpath:org/springframework/webflow/persistence/persistence.xml");
		OpenJpaVendorAdapter openJpa = new OpenJpaVendorAdapter();
		factory.setJpaVendorAdapter(openJpa);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

}

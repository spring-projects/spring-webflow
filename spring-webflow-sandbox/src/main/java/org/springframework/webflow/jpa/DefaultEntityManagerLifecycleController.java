package org.springframework.webflow.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.orm.jpa.EntityManagerHolder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Default abstract JPA implementation for {@link EntityManagerLifecycleController}.
 *
 * @author Maxim Petrashev
 */
public abstract class DefaultEntityManagerLifecycleController implements EntityManagerLifecycleController {
    private EntityManagerFactory _entityManagerFactory;

    public DefaultEntityManagerLifecycleController(EntityManagerFactory aEntityManagerFactory) {
        _entityManagerFactory = aEntityManagerFactory;
    }

    public void close(EntityManager aEntityManager) {
        Assert.isTrue(aEntityManager.isOpen(), "Entity manager was already closed");
        aEntityManager.close();
    }

    public void deactivate(EntityManager aEntityManager) {
        unbind(aEntityManager);
        if (_log.isDebugEnabled()) {
            _log.debug("Entity manager unbinded: " + aEntityManager.isOpen());
        }
    }

    public EntityManager create() {
        return _entityManagerFactory.createEntityManager();
    }

    public void flush(EntityManager aEntityManager) {
        try {
            aEntityManager.flush();
        } catch (RuntimeException e) {//todo review this code
            aEntityManager.getTransaction().rollback();
            throw e;
        }
    }

    public void activate(EntityManager aEntityManager) {
        bind(aEntityManager);
        if (_log.isDebugEnabled()) {
            _log.debug("Session activate: " + aEntityManager.isOpen());
        }
    }
    protected void bind(EntityManager aEntityManager) {
        TransactionSynchronizationManager.bindResource(_entityManagerFactory, new EntityManagerHolder(aEntityManager));
    }

    protected void unbind( EntityManager aEntityManager ) {
        synchronized(_entityManagerFactory){//todo is this need?
            Assert.isTrue( TransactionSynchronizationManager.hasResource(_entityManagerFactory) ); //todo remove this code. Resource must be already present.
            TransactionSynchronizationManager.unbindResource(_entityManagerFactory);
        }
    }

    protected EntityManagerFactory getEntityManagerFactory() {
        return _entityManagerFactory;
    }

    protected final Log _log = LogFactory.getLog(getClass());
}

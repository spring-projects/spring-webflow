package org.springframework.webflow.jpa;

import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.util.AbstractReadOnlyResourceHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;

/**
 * Resource holder that is wrapper for TransactionSynchronizationManager.
 *
 * @author Maxim Petrashev
 */
public class TransactionSynchronizationManagerEnityManagerHolder extends AbstractReadOnlyResourceHolder<EntityManager> {
    public EntityManager get() {
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource( _key );
        return emHolder.getEntityManager();
    }

    public TransactionSynchronizationManagerEnityManagerHolder(Object aKey) {
        _key = aKey;
    }

    private Object _key;
}

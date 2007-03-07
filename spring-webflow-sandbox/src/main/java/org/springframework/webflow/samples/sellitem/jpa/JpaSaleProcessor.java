package org.springframework.webflow.samples.sellitem.jpa;

import org.springframework.webflow.samples.sellitem.SaleProcessor;
import org.springframework.webflow.samples.sellitem.Sale;

import javax.persistence.EntityManager;

public abstract class JpaSaleProcessor implements SaleProcessor {
    public void process(Sale sale) {
        EntityManager entityManager = getEntityManager();
        entityManager.persist(sale);
    }
    protected abstract EntityManager getEntityManager();
}

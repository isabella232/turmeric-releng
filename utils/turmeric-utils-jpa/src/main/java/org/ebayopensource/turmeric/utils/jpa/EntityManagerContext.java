/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.jpa;

import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class EntityManagerContext {
    private static final ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();
    private static final ThreadLocal<AtomicInteger> reentrancy = new ThreadLocal<AtomicInteger>() {
        @Override
        protected AtomicInteger initialValue() {
            return new AtomicInteger();
        }
    };

    public static EntityManager get() {
        return entityManager.get();
    }

    public static void open(EntityManagerFactory entityManagerFactory) {
        if (get() == null) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityManagerContext.entityManager.set(entityManager);
            entityManager.getTransaction().begin();
        }
        reentrancy.get().incrementAndGet();
    }

    static void abort() {
        EntityManager entityManager = get();
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction != null) {
            transaction.setRollbackOnly();
        }
    }

    public static void close() {
        boolean close = reentrancy.get().decrementAndGet() == 0;
        if (close) {
            EntityManager entityManager = get();
            EntityManagerContext.entityManager.set(null);
            EntityTransaction transaction = entityManager.getTransaction();
            if (transaction != null) {
                if (transaction.getRollbackOnly()) {
                    rollback(transaction);
                } else {
                    try {
                        transaction.commit();
                    } catch (RuntimeException x) {
                        rollback(transaction);
                    }
                }
            }
        }
    }

    private static void rollback(EntityTransaction transaction) {
        try {
            transaction.rollback();
        } catch (RuntimeException x) {
            // Ignored
        }
    }
}

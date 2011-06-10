/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceContext {
    private static final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<String, EntityManagerFactory>();

    public static synchronized EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
        EntityManagerFactory entityManagerFactory = entityManagerFactories.get(persistenceUnitName);
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
            entityManagerFactories.put(persistenceUnitName, entityManagerFactory);
        }
        return entityManagerFactory;
    }

    public static synchronized void destroyEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        for (Iterator<EntityManagerFactory> iterator = entityManagerFactories.values().iterator(); iterator.hasNext();) {
            EntityManagerFactory factory = iterator.next();
            if (factory == entityManagerFactory) {
                entityManagerFactory.close();
                iterator.remove();
                break;
            }
        }
    }

    public static synchronized void close() {
        for (EntityManagerFactory entityManagerFactory : entityManagerFactories.values()) {
            entityManagerFactory.close();
        }
        entityManagerFactories.clear();
    }
}

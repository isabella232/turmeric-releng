/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.Table;

public class AbstractDAO {
    protected EntityManager getEntityManager() {
        return EntityManagerContext.get();
    }

    protected void persistEntity(Object entity) {
        EntityManager entityManager = getEntityManager();
        entityManager.persist(entity);
        entityManager.flush();
    }

    protected void persistEntities(List<?> entities) {
        EntityManager entityManager = getEntityManager();
        for (Object entity : entities)
            entityManager.persist(entity);
        entityManager.flush();
    }

    protected <T> T findEntity(Class<T> klass, long id) {
        EntityManager entityManager = getEntityManager();
        return entityManager.find(klass, id);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> List<T> findEntityByMemberId(Class<T> klass, 
                    String type, String typeValue, String member, long id)
    {
        StringBuilder jpql = new StringBuilder();
        jpql.append("select e from ").append(klass.getName()).append(" as e");
        jpql.append(" join e.").append(member).append(" as m");
        jpql.append(" where e.").append(type).append(" = :type");
        jpql.append(" and m.id = :id");

        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("type", typeValue);
        query.setParameter("id", id);
        
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> findEntityByMemberValue(Class<T> klass, 
                    String type, String typeValue, 
                    String member, String field, String fieldValue)
    {
        StringBuilder jpql = new StringBuilder();
        jpql.append("select e from ").append(klass.getName()).append(" as e");
        jpql.append(" join e.").append(member).append(" as m");
        jpql.append(" where e.").append(type).append(" = :type");
        jpql.append(" and m.").append(field).append("= :value");

        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("type", typeValue);
        query.setParameter("value", fieldValue);
        
        return query.getResultList();
    }

    protected boolean removeEntity(Class<?> klass, long id) {
        EntityManager entityManager = getEntityManager();
        Object entity = entityManager.find(klass, id);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }

    protected <T> T getSingleResultOrNull(Query query) {
        @SuppressWarnings("unchecked")
        List<T> results = query.getResultList();
        if (results.isEmpty()) return null;
        if (results.size() > 1) throw new NonUniqueResultException();
        return results.get(0);
    }

    protected <T> T getSingleResultOrNull(Class<T> klass, String fieldName, Object fieldValue) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("from ").append(klass.getName()).append(" as e");
        jpql.append(" where e.").append(fieldName).append(" = :value");

        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("value", fieldValue);

        return (T)getSingleResultOrNull(query);
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getResultList(Class<T> klass, String fieldName, Object fieldValue) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("from ").append(klass.getName()).append(" as e");
        jpql.append(" where e.").append(fieldName).append(" = :value");

        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("value", fieldValue);

        return query.getResultList();
    }


    @SuppressWarnings("unchecked")
    protected <T> List<T> getResultList(Class<T> klass, 
                                        String fieldName1, Object fieldValue1,
                                        String fieldName2, Object fieldValue2,
                                        String fieldName3, Object lowValue3, Object highValue3) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("from ").append(klass.getName()).append(" as e");
        jpql.append(" where e.").append(fieldName1).append(" = :value1");
        jpql.append(" and e.").append(fieldName2).append(" = :value2");
        jpql.append(" and e.").append(fieldName3).append(" >= :low");
        jpql.append(" and e.").append(fieldName3).append(" <= :high");

        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("value1", fieldValue1);
        query.setParameter("value2", fieldValue2);
        query.setParameter("low", lowValue3);
        query.setParameter("high", highValue3);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getWildcardResultList(Class<T> klass, 
                                                String type, String typeValue, 
                                                String fieldName, String wildcard) {
        if (wildcard == null || wildcard.isEmpty()) {
            wildcard = "%";
        }
        
        StringBuilder jpql = new StringBuilder();
        jpql.append("from ").append (klass.getName()).append(" as e");
        jpql.append(" where e.").append(type).append(" = :type");
        jpql.append(" and e.").append(fieldName).append(" like ");
        jpql.append("'").append(wildcard).append("'");

        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("type", typeValue);

        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    protected <T> List<T> getWildcardResultList(Class<T> klass, 
                                                String fieldName, String wildcard) {
        if (wildcard == null || wildcard.isEmpty()) {
            wildcard = "%";
        }
        
        StringBuilder jpql = new StringBuilder();
        jpql.append("from ").append (klass.getName()).append(" as e");
        jpql.append(" where e.").append(fieldName).append(" like ");
        jpql.append("'").append(wildcard).append("'");

        EntityManager entityManager = getEntityManager();
        Query query = entityManager.createQuery(jpql.toString());
        
        return query.getResultList();
    }
    
}

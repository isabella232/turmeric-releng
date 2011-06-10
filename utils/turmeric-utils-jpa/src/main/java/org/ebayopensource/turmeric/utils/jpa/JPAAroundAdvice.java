/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.jpa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManagerFactory;

public class JPAAroundAdvice implements InvocationHandler {
    private final EntityManagerFactory entityManagerFactory;
    private final Object target;

    public JPAAroundAdvice(EntityManagerFactory entityManagerFactory, Object target) {
        this.entityManagerFactory = entityManagerFactory;
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass == Object.class) {
            return handleObjectMethod(proxy, method, args);
        } else if (method.getDeclaringClass().isInstance(target)) {
            return handleProxiedMethod(method, args);
        } else {
            throw new NoSuchMethodException(method.toString());
        }
    }

    private Object handleProxiedMethod(Method method, Object[] args) throws Throwable {
        try {
            EntityManagerContext.open(entityManagerFactory);
            return method.invoke(target, args);
        } catch (InvocationTargetException x) {
            EntityManagerContext.abort();
            throw x.getCause();
        } finally {
            EntityManagerContext.close();
        }
    }

    private Object handleObjectMethod(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("equals".equals(methodName)) {
            return proxyEquals(proxy, args[0]);
        } else if ("hashCode".equals(methodName)) {
            return proxyHashCode(proxy);
        } else if ("toString".equals(methodName)) {
            return proxyToString(proxy);
        } else {
            throw new NoSuchMethodException(method.toString());
        }
    }

    private boolean proxyEquals(Object obj1, Object obj2) {
        return obj1 == obj2;
    }

    private int proxyHashCode(Object proxy) {
        return System.identityHashCode(proxy);
    }

    private String proxyToString(Object proxy) {
        return proxy.getClass().getName() + "@" + Integer.toHexString(proxy.hashCode());
    }
}

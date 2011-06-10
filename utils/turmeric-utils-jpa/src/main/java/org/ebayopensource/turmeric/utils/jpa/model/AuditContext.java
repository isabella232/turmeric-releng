/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.jpa.model;

public class AuditContext {
    private final static ThreadLocal<String> currentUser = new ThreadLocal<String>();

    public static String getUser() {
        return currentUser.get();
    }

    public static void setUser(String user) {
        currentUser.set(user);
    }

    public static void clear() {
        currentUser.set(null);
    }
}

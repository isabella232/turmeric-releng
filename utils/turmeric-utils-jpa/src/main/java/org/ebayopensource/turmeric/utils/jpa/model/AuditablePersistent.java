/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.jpa.model;

import java.util.Date;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@MappedSuperclass
public class AuditablePersistent extends Persistent {
    private AuditInfo auditInfo = new AuditInfo();

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    @PrePersist
    protected void persistAuditInfo() {
        auditInfo.setCreatedBy(AuditContext.getUser());
        auditInfo.setCreatedOn(new Date());
    }

    @PreUpdate
    protected void updateAuditInfo() {
        auditInfo.setUpdatedBy(AuditContext.getUser());
        auditInfo.setUpdatedOn(new Date());
    }
}

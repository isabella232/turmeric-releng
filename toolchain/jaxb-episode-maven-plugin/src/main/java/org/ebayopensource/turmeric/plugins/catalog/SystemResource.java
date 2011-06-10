/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.catalog;

import java.io.PrintWriter;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;

public class SystemResource {
    private String systemId;
    private String resource;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
        result = prime * result + ((resource == null) ? 0 : resource.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SystemResource other = (SystemResource) obj;
        if (resource == null) {
            if (other.resource != null) {
                return false;
            }
        }
        else if (!resource.equals(other.resource)) {
            return false;
        }
        if (systemId == null) {
            if (other.systemId != null) {
                return false;
            }
        }
        else if (!systemId.equals(other.systemId)) {
            return false;
        }
        return true;
    }

    public void writeXml(PrintWriter out) throws MojoExecutionException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(resource);
        if (url == null) {
            throw new MojoExecutionException("Unable to find classpath resource in project dependencies: " + resource);
        }
        out.printf("  <system systemId=\"%s\"", systemId);
        out.printf(" uri=\"%s\" />%n", url);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<system-resource");
        builder.append(String.format(" systemId=\"%s\"", systemId));
        builder.append(String.format(" resource=\"%s\"", resource));
        builder.append(" />");
        return builder.toString();
    }
}

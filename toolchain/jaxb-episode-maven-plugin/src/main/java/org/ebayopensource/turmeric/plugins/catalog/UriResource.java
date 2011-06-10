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

public class UriResource {
    private String name;
    private String resource;

    public String getName() {
        return name;
    }

    public void setName(String uri) {
        this.name = uri;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        UriResource other = (UriResource) obj;
        if (resource == null) {
            if (other.resource != null) {
                return false;
            }
        }
        else if (!resource.equals(other.resource)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!name.equals(other.name)) {
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
        out.printf("  <uri name=\"%s\"", name);
        out.printf(" uri=\"%s\" />%n", url);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<uri-resource");
        builder.append(String.format(" name=\"%s\"", name));
        builder.append(String.format(" resource=\"%s\"", resource));
        builder.append(" />");
        return builder.toString();
    }
}

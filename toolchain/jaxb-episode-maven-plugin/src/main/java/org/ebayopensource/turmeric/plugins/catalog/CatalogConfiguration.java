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
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

public class CatalogConfiguration {
    private Set<PublicResource> publicResources = new LinkedHashSet<PublicResource>();
    private Set<SystemResource> systemResources = new LinkedHashSet<SystemResource>();
    private Set<UriResource> uriResources = new LinkedHashSet<UriResource>();

    public Set<UriResource> getUriResources() {
        return uriResources;
    }

    public void setUriResources(Set<UriResource> uriResources) {
        this.uriResources = uriResources;
    }

    public void addUriResource(UriResource resource) {
        this.uriResources.add(resource);
    }

    public Set<PublicResource> getPublicResources() {
        return publicResources;
    }

    public void setPublicResources(Set<PublicResource> publicResources) {
        this.publicResources = publicResources;
    }

    public void addPublicResource(PublicResource resource) {
        publicResources.add(resource);
    }

    public Set<SystemResource> getSystemResources() {
        return systemResources;
    }

    public void setSystemResources(Set<SystemResource> systemResources) {
        this.systemResources = systemResources;
    }

    public void addSystemResource(SystemResource resource) {
        systemResources.add(resource);
    }

    public void writeXml(PrintWriter out) throws MojoExecutionException {
        out.printf("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>%n");

        // Catalog 1.1
        // out.printf("<!DOCTYPE catalog%n");
        // out.printf("  PUBLIC \"-//OASIS//DTD XML Catalogs V1.1//EN\"%n");
        // out.printf("         \"http://www.oasis-open.org/committees/entity/release/1.1/catalog.dtd\">%n");
        
        // Catalog 1.0
        out.printf("<!DOCTYPE catalog%n");
        out.printf("  PUBLIC \"-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN\"%n");
        out.printf("         \"http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd\">%n");
        
        out.printf("<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\" prefer=\"public\">%n");
        for (PublicResource ref : publicResources) {
            ref.writeXml(out);
        }
        for (SystemResource ref : systemResources) {
            ref.writeXml(out);
        }
        for (UriResource ref : uriResources) {
            ref.writeXml(out);
        }
        out.printf("</catalog>%n");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<catalog>");
        for (PublicResource ref : publicResources) {
            builder.append("\n  ").append(ref);
        }
        for (SystemResource ref : systemResources) {
            builder.append("\n  ").append(ref);
        }
        for (UriResource ref : uriResources) {
            builder.append("\n  ").append(ref);
        }
        builder.append("\n</catalog>");
        return builder.toString();
    }
}

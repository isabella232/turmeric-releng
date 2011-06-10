/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.banrefs.filters;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.StringUtils;

public class TextFileFilter implements FileFilter {
    public static final TextFileFilter INSTANCE = new TextFileFilter();
    private Set<String> knownTextFileExtensions = new HashSet<String>();

    public TextFileFilter() {
        knownTextFileExtensions.add("txt");
        knownTextFileExtensions.add("htm");
        knownTextFileExtensions.add("html");
        knownTextFileExtensions.add("xml");
        knownTextFileExtensions.add("xsd");
        knownTextFileExtensions.add("wsdl");
        knownTextFileExtensions.add("episode");
        knownTextFileExtensions.add("java");
        knownTextFileExtensions.add("properties");
        knownTextFileExtensions.add("conf");
        knownTextFileExtensions.add("config");
        knownTextFileExtensions.add("ini");
        knownTextFileExtensions.add("bat");
        knownTextFileExtensions.add("cmd");
        knownTextFileExtensions.add("sh");
        knownTextFileExtensions.add("jsp");
        knownTextFileExtensions.add("js");
        knownTextFileExtensions.add("rss");
        knownTextFileExtensions.add("pl");
        knownTextFileExtensions.add("css");
        knownTextFileExtensions.add("mf");
        knownTextFileExtensions.add("pom");
        knownTextFileExtensions.add("xsl");
        knownTextFileExtensions.add("xslt");
        knownTextFileExtensions.add("jelly");
    }

    @Override
    public boolean accept(File path) {
        if (!path.isFile()) {
            return false;
        }

        String filename = path.getName().toLowerCase();
        if (filename.charAt(0) == '.') {
            // Unix hidden.
            return false;
        }

        String ext = FilenameUtils.getExtension(filename);
        if (StringUtils.isBlank(ext)) {
            return false;
        }

        return knownTextFileExtensions.contains(ext);
    }

}

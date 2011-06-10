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

/**
 * {@link FileFilter} to accept directories.
 * <p>
 * Rejects directories that are:
 * <ul>
 * <li>Hidden</li>
 * <li>Part of an SCM control structure (scm/git)</li>
 * <li>Called "target" (part of the build)</li>
 * </ul>
 */
public class DirectoryFilter implements FileFilter {
    public static final DirectoryFilter INSTANCE = new DirectoryFilter();

    @Override
    public boolean accept(File path) {
        if (!path.isDirectory()) {
            return false;
        }

        if (path.isHidden()) {
            return false;
        }

        String name = path.getName().toLowerCase();

        if (name.equals("target") || name.equals(".svn") || name.equals(".git") || name.equals("gen-src") || name.equals("gen-meta-src") || name.equals("test-data") || name.equals("fixtures") || name.equals("lib")) {
            return false;
        }
        
        // Many false positives are occurring in the Test directory because of input files.
        if (name.contains("Test") || name.contains("test")) {
        	return false;
        }

        return true;
    }
}

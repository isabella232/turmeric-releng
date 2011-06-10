/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.license.filters;

import java.io.File;
import java.io.FileFilter;

public class FileExtensionFilter implements FileFilter {
    private String[] extensions;

    public FileExtensionFilter(String[] extensions) {
        this.extensions = extensions;
    }

    @Override
    public boolean accept(File path) {
        if (!path.isFile()) {
            return false;
        }

        String name = path.getName();
        int idx = name.lastIndexOf('.');
        if (idx == (-1)) {
            // No extension.
            return false;
        }
        String ext = name.substring(idx).toLowerCase();

        for (String expected : extensions) {
            if (ext.equals(expected)) {
                return true;
            }
        }

        return false;
    }
}

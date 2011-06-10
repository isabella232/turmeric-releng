/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.config.reports;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.project.MavenProject;
import org.ebayopensource.turmeric.maven.config.console.Console;

public class ConsoleReport extends AbstractReport {
    private String basedir;
    private File activeFile = null;
    private boolean hasError = false;

    public ConsoleReport(Console console, MavenProject project) {
        super(console);
        this.basedir = FilenameUtils.normalize(project.getBasedir().getAbsolutePath());
    }

    @Override
    public void fileStart(File file) {
        super.fileStart(file);
        this.activeFile = file;
        this.hasError = false;
    }

    private void printFileHeader() {
        if (!hasError) {
            System.out.printf("%d)  %s%n", this.getFileViolationCount(), relative(activeFile));
        }
        hasError = true;
    }

    @Override
    public void violation(String context, String format, Object... args) {
        printFileHeader();
        System.out.printf("   Context: %s%n", context);
        System.out.printf("   %s%n", String.format(format, args));
    }

    private String relative(File file) {
        String path = file.getAbsolutePath();
        if (path.startsWith(basedir)) {
            return path.substring(basedir.length());
        }
        return path;
    }
}

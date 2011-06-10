/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.license.reports;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.project.MavenProject;
import org.ebayopensource.turmeric.maven.license.Report;

public class ConsoleReport implements Report {
    private String basedir;
    private int entryCount, passCount, violationCount, failureCount;

    public ConsoleReport(MavenProject project) {
        this.basedir = FilenameUtils.normalize(project.getBasedir().getAbsolutePath());
        this.entryCount = 0;
        this.passCount = 0;
        this.violationCount = 0;
        this.failureCount = 0;
    }

    @Override
    public void close() {
        System.out.printf("Scanned %d files [%d passed] [%d violations] [%d failures]%n", entryCount, passCount,
                        violationCount, failureCount);
    }

    private String relative(File file) {
        String path = file.getAbsolutePath();
        if (path.startsWith(basedir)) {
            return path.substring(basedir.length());
        }
        return path;
    }

    @Override
    public void failure(File file, Throwable t) {
        this.entryCount++;
        this.failureCount++;

        System.out.printf("%d) %s%n", failureCount, relative(file));
        System.out.print("   EXCEPTION: ");
        t.printStackTrace(System.out);
    }

    @Override
    public void violation(File file, String type, int lineNum, String format, Object... args) {
        this.entryCount++;
        this.violationCount++;

        System.out.printf("%d) [%s] %s%n", violationCount, type.toUpperCase(), relative(file));
        System.out.printf("   " + format + "%n", args);
    }

    @Override
    public void pass(File file) {
        this.entryCount++;
        this.passCount++;
    }
}

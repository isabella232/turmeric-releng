/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.banrefs.reports;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.maven.banrefs.Report;

public class ConsoleReport implements Report {
    private String basedir;
    private int entryCount, errorCount;
    private File activeFile = null;
    private boolean hasError = false;

    public ConsoleReport(MavenProject project) {
        this.basedir = FilenameUtils.normalize(project.getBasedir().getAbsolutePath());
        this.entryCount = 0;
        this.errorCount = 0;
    }

    @Override
    public void close() {
        System.out.printf("Scanned %d files [%d passed] [%d errors]%n", entryCount, (entryCount - errorCount), errorCount);
    }

    private String relative(File file) {
        String path = file.getAbsolutePath();
        if (path.startsWith(basedir)) {
            return path.substring(basedir.length());
        }
        return path;
    }

    @Override
    public void fileStart(File file) {
        activeFile = file;
        hasError = false;
    }

    private void printFileHeader() {
        if (!hasError) {
            System.out.printf("%d) %s%n", errorCount, relative(activeFile));
        }
        hasError = true;
    }

    @Override
    public void fileEnd() {
        /* do nothing */
    }

    @Override
    public void exception(Throwable t) {
        this.entryCount++;
        this.errorCount++;

        printFileHeader();

        System.out.print("   EXCEPTION: ");
        t.printStackTrace(System.out);
    }

    @Override
    public void error(int linenum, String match, int startOffset, int endOffset, String line) {
        this.entryCount++;
        this.errorCount++;

        printFileHeader();

        System.out.printf("   (Line #%d): BANNED REF \"%s\" Offset(%d thru %d)%n", linenum, match, startOffset,
                        endOffset);
        System.out.println(line.replace('\t', ' '));
        System.out.print(StringUtils.repeat(" ", startOffset));
        System.out.print(StringUtils.repeat("^", endOffset - startOffset));
        System.out.println();
    }
}

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

import org.ebayopensource.turmeric.maven.config.console.Console;
import org.ebayopensource.turmeric.runtime.config.validation.Report;

public abstract class AbstractReport implements Report {
    private Console console;
    private int fileCount;
    private int violationCount;
    private int fileViolationCount;
    private boolean hasViolation;

    public AbstractReport(Console console) {
        this.console = console;
        this.fileCount = 0;
        this.violationCount = 0;
        this.fileViolationCount = 0;
    }

    @Override
    public int getFileCount() {
        return this.fileCount;
    }

    @Override
    public int getFileViolationCount() {
        return this.fileViolationCount;
    }

    @Override
    public int getViolationCount() {
        return this.violationCount;
    }

    @Override
    public void fileStart(File file) {
        this.hasViolation = false;
        this.fileCount++;
    }

    @Override
    public void fileEnd() {
        if (this.hasViolation) {
            this.fileViolationCount++;
        }
    }

    public void close() {
        console.printf("Scanned %d files [%d passed] [%d violations]", fileCount,
                        (fileCount - fileViolationCount), violationCount);
    }
}

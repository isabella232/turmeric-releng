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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.codehaus.plexus.util.IOUtil;
import org.ebayopensource.turmeric.maven.license.LicenseCheckMojo;
import org.ebayopensource.turmeric.maven.license.Report;
import org.ebayopensource.turmeric.maven.license.util.Console;
import org.ebayopensource.turmeric.maven.license.util.XmlUtil;

public class CheckstyleXmlReport implements Report {
    private Console console;
    private FileWriter writer;
    private PrintWriter out;
    private int entryCount, passCount, violationCount, failureCount;

    public CheckstyleXmlReport(Console console, File reportFile) throws IOException {
        this.console = console;
        this.entryCount = 0;
        this.passCount = 0;
        this.violationCount = 0;
        this.failureCount = 0;
        this.writer = new FileWriter(reportFile);
        this.out = new PrintWriter(writer);
        this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.out.printf("<checkstyle version=\"5.0\">%n");
    }
    
    @Override
    public void close() {
        out.println("</checkstyle>");
        IOUtil.close(out);
        IOUtil.close(writer);

        console.printf("Scanned %d files [%d passed] [%d violations] [%d failures]", entryCount, passCount,
                        violationCount, failureCount);
    }

    @Override
    public void failure(File file, Throwable t) {
        this.entryCount++;
        this.failureCount++;
        
        out.printf("<file name=\"%s\">%n", XmlUtil.escaped(file));
        out.printf("<error line=\"0\" severity=\"error\"");
        out.printf(" message=\"%s: %s\"", XmlUtil.escaped(t.getClass().getName()), XmlUtil.escaped(t.getMessage()));
        out.printf(" source=\"%s\" />%n", XmlUtil.escaped(LicenseCheckMojo.class));
        out.printf("</file>%n");
        out.flush();
    }

    @Override
    public void violation(File file, String type, int lineNum, String format, Object... args) {
        this.entryCount++;
        this.violationCount++;
        
        out.printf("<file name=\"%s\">%n", XmlUtil.escaped(file));
        out.printf("<error line=\"%d\" severity=\"error\"", lineNum);
        out.printf(" message=\"[%s] %s\"", type.toUpperCase(), XmlUtil.escaped(format, args));
        out.printf(" source=\"%s\" />%n", XmlUtil.escaped(LicenseCheckMojo.class));
        out.printf("</file>%n");
        out.flush();
        out.flush();
    }

    @Override
    public void pass(File file) {
        this.entryCount++;
        this.passCount++;
        
        out.printf("<file name=\"%s\">%n</file>%n", file);
        out.flush();
    }
}

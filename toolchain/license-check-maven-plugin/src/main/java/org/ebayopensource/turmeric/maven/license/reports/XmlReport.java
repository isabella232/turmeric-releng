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
import java.io.StringWriter;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.ebayopensource.turmeric.maven.license.Report;
import org.ebayopensource.turmeric.maven.license.util.Console;
import org.ebayopensource.turmeric.maven.license.util.XmlUtil;

public class XmlReport implements Report {
    private Console console;
    private FileWriter writer;
    private PrintWriter out;
    private int entryCount, passCount, violationCount, failureCount;

    public XmlReport(Console console, MavenProject project, File outputFile) throws IOException {
        this.console = console;
        this.entryCount = 0;
        this.passCount = 0;
        this.violationCount = 0;
        this.failureCount = 0;
        this.writer = new FileWriter(outputFile);
        this.out = new PrintWriter(writer);
        this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.out.printf("<license-report module-root=\"%s\" module-name=\"%s\">%n", 
                        XmlUtil.escaped(project.getBasedir().getAbsolutePath()),
                        XmlUtil.escaped(project.getName()));
    }
    
    @Override
    public void close() {
        out.println("</license-report>");
        IOUtil.close(out);
        IOUtil.close(writer);

        console.printf("Scanned %d files [%d passed] [%d violations] [%d failures]", entryCount, passCount,
                        violationCount, failureCount);
    }

    @Override
    public void failure(File file, Throwable t) {
        this.entryCount++;
        this.failureCount++;
        out.printf("<file file=\"%s\" violation=\"exception\" line=\"0\">%n", XmlUtil.escaped(file));
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        out.println(XmlUtil.escaped(sw.toString()));
        out.printf("</file>%n");
        out.flush();
    }

    @Override
    public void violation(File file, String type, int lineNum, String format, Object... args) {
        this.entryCount++;
        this.violationCount++;
        out.printf("<file file=\"%s\" type=\"%s\" line=\"%d\">%n", XmlUtil.escaped(file), type, lineNum);
        out.println(XmlUtil.escaped(format, args));
        out.printf("</file>%n");
        out.flush();
    }

    @Override
    public void pass(File file) {
        this.entryCount++;
        this.passCount++;
        out.printf("<file file=\"%s\" violation=\"none\" line=\"0\" />%n", XmlUtil.escaped(file));
        out.flush();
    }
}

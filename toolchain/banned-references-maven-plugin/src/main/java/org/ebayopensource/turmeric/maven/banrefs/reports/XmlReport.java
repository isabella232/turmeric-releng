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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.ebayopensource.turmeric.maven.banrefs.Report;
import org.ebayopensource.turmeric.maven.banrefs.console.Console;

public class XmlReport implements Report {
    private Console console;
    private FileWriter writer;
    private PrintWriter out;
    private int entryCount, errorCount;
    private File activeFile;
    private boolean hasError = false;

    public XmlReport(Console console, MavenProject project, File outputFile) throws IOException {
        this.console = console;
        this.entryCount = 0;
        this.errorCount = 0;
        this.writer = new FileWriter(outputFile);
        this.out = new PrintWriter(writer);
        this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.out.printf("<banned-references-report module-root=\"%s\" module-name=\"%s\">%n",
                        XmlUtil.escaped(project.getBasedir().getAbsolutePath()), XmlUtil.escaped(project.getName()));
    }

    @Override
    public void close() {
        out.println("</banned-references-report>");
        IOUtil.close(out);
        IOUtil.close(writer);

        console.printf("Scanned %d files [%d passed] [%d errors]", entryCount, (entryCount - errorCount), errorCount);
    }

    @Override
    public void fileStart(File file) {
        this.activeFile = file;
        this.hasError = false;
    }

    @Override
    public void fileEnd() {
        if (!hasError) {
            out.printf("<file name=\"%s\">%n", XmlUtil.escaped(activeFile));
        }

        out.println("</file>");
    }

    @Override
    public void exception(Throwable t) {
        if (!hasError) {
            out.printf("<file name=\"%s\">%n", XmlUtil.escaped(activeFile));
        }
        hasError = true;
        out.printf("<error linenumber=\"0\">%n");
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        out.println(XmlUtil.escaped(sw.toString()));
        out.printf("</error>%n");
    }

    @Override
    public void error(int linenum, String match, int startOffset, int endOffset, String line) {
        if (!hasError) {
            out.printf("<file name=\"%s\">%n", XmlUtil.escaped(activeFile));
        }
        hasError = true;
        out.printf("<error linenumber=\"%d\"", linenum);
        out.printf(" match=\"%s\"", XmlUtil.escaped(match));
        out.printf(" offset-start=\"%d\" offset-end=\"%s\">%n", startOffset, endOffset);
        out.println(XmlUtil.escaped(line));
        out.printf("</error>%n");
    }
}

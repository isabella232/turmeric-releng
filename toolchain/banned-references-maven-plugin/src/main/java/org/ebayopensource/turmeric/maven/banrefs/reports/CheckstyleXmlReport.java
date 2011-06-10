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

import org.codehaus.plexus.util.IOUtil;
import org.ebayopensource.turmeric.maven.banrefs.BannedRefsMojo;
import org.ebayopensource.turmeric.maven.banrefs.Report;
import org.ebayopensource.turmeric.maven.banrefs.console.Console;

public class CheckstyleXmlReport implements Report {
    private Console console;
    private FileWriter writer;
    private PrintWriter out;
    private int entryCount, errorCount;

    public CheckstyleXmlReport(Console console, File outputFile) throws IOException {
        this.console = console;
        this.entryCount = 0;
        this.errorCount = 0;
        this.writer = new FileWriter(outputFile);
        this.out = new PrintWriter(writer);
        this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.out.printf("<checkstyle version=\"5.0\">%n");
    }

    @Override
    public void close() {
        out.println("</checkstyle>");
        IOUtil.close(out);
        IOUtil.close(writer);

        console.printf("Scanned %d files [%d passed] [%d errors]", entryCount, (entryCount - errorCount), errorCount);
    }

    @Override
    public void fileStart(File file) {
        out.printf("<file name=\"%s\">%n", XmlUtil.escaped(file));
    }

    @Override
    public void fileEnd() {
        out.println("</file>");
    }

    @Override
    public void exception(Throwable t) {
        out.printf("<error line=\"%d\"", 0);
        out.printf(" severity=\"warning\"");
        out.printf(" message=\"%s: %s\"", XmlUtil.escaped(t.getClass().getName()), XmlUtil.escaped(t.getMessage()));
        out.printf(" source=\"%s\" />%n", XmlUtil.escaped(BannedRefsMojo.class));
    }
    
    @Override
    public void error(int linenum, String match, int startOffset, int endOffset, String line) {
        out.printf("<error line=\"%d\"", linenum);
        out.printf(" severity=\"warning\"");
        out.printf(" message=\"Line matches the illegal pattern &apos;%s&apos; at offset:%d\"", match, startOffset);
        out.printf(" source=\"com.puppycrawl.tools.checkstyle.checks.regexp.RegexpSinglelineCheck\" />%n");
    }
}

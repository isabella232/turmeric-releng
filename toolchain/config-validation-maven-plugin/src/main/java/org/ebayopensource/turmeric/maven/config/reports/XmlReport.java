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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.ebayopensource.turmeric.maven.config.console.Console;

public class XmlReport extends AbstractReport {
    private FileWriter writer;
    private PrintWriter out;

    public XmlReport(Console console, MavenProject project, File outputFile) throws IOException {
        super(console);
        this.writer = new FileWriter(outputFile);
        this.out = new PrintWriter(writer);
        this.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.out.printf("<config-validation-report module-root=\"%s\" module-name=\"%s\">%n",
                        XmlUtil.escaped(project.getBasedir().getAbsolutePath()),
                        XmlUtil.escaped(project.getName()));
    }

    @Override
    public void fileEnd() {
        super.fileEnd();
        out.println("</file>");
    }

    @Override
    public void fileStart(File file) {
        super.fileStart(file);
        out.printf("  <file name=\"%s\">%n", XmlUtil.escaped(file));
    }

    @Override
    public void violation(String context, String format, Object... args) {
        out.printf("    <violation context=\"%s\">%n", XmlUtil.escaped(context));
        out.printf("    %s%n", XmlUtil.escaped(String.format(format, args)));
        out.printf("    </violation>%n");
    }

    public void close() {
        super.close();
        out.println("</config-validation-report>");
        IOUtil.close(out);
        IOUtil.close(writer);
    }
}

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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.maven.license.Report;

public class JunitReport implements Report {
    public class FileEntry {
        public File file;
        public String type;
        public int lineNum;
        public String msg;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("FileEntry [file=");
            builder.append(file);
            builder.append("\n type=");
            builder.append(type);
            builder.append("\n lineNum=");
            builder.append(lineNum);
            builder.append("\n msg=");
            builder.append(msg);
            return builder.toString();
        }
    }

    private Map<File, FileEntry> files = new HashMap<File, JunitReport.FileEntry>();

    @Override
    public void close() {
        System.out.printf("Report has %d entries%n", files.size());
    }

    public int getEntryCount() {
        return files.size();
    }

    @Override
    public void failure(File file, Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));

        FileEntry entry = new FileEntry();
        entry.file = file;
        entry.type = "exception";
        entry.msg = sw.toString();
        entry.lineNum = 0;

        files.put(file, entry);
    }

    @Override
    public void violation(File file, String type, int lineNum, String format, Object... args) {
        FileEntry entry = new FileEntry();
        entry.file = file;
        entry.type = type;
        entry.msg = String.format(format, args);
        entry.lineNum = lineNum;

        files.put(file, entry);
    }

    @Override
    public void pass(File file) {
        FileEntry entry = new FileEntry();
        entry.file = file;
        entry.type = "none";
        entry.msg = null;
        entry.lineNum = 0;

        files.put(file, entry);
    }

    public FileEntry getEntry(File file) {
        return files.get(file);
    }
}

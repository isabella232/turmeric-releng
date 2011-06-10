/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.license.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.maven.license.Report;

public class HeaderCheck {
    private String header[];
    private boolean optional[];

    public HeaderCheck(String regexId) throws MojoExecutionException {
        URL url = this.getClass().getClassLoader().getResource(regexId);
        if (url == null) {
            throw new MojoExecutionException("Unable to find regex header: " + regexId);
        }

        List<String> regii = new ArrayList<String>();

        InputStream in = null;
        InputStreamReader reader = null;
        BufferedReader buf = null;

        try {
            in = url.openStream();
            reader = new InputStreamReader(in);
            buf = new BufferedReader(reader);

            String line;
            while ((line = buf.readLine()) != null) {
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                regii.add(line);
            }

            this.header = regii.toArray(new String[0]);
            this.optional = new boolean[header.length];
            int idx;
            String suffix;
            for (int i = 0; i < header.length; i++) {
                optional[i] = false;
                idx = header[i].indexOf('$');
                if (idx == (-1)) {
                    // Skip, No end of line char '$'
                    continue;
                }
                if (idx == header[i].length() - 1) {
                    // Skip, at end of line.
                    continue;
                }
                suffix = header[i].substring(idx + 1);
                optional[i] = (suffix.equals("@OPTIONAL@"));
                header[i] = header[i].substring(0, idx + 1);
            }
        }
        catch (IOException e) {
            throw new MojoExecutionException("Unable to load regex header: " + regexId, e);
        }
        finally {
            IOUtil.close(buf);
            IOUtil.close(reader);
            IOUtil.close(in);
        }
    }

    public void check(Report report, File file) {
        try {
            String fileHeader[] = readFileHeader(file);

            int maxLines = Math.min(header.length, fileHeader.length);

            for (int fileIdx = 0, regexIdx = 0; regexIdx < maxLines; fileIdx++, regexIdx++) {
                while (!fileHeader[fileIdx].matches(header[regexIdx]) && optional[regexIdx]) {
                    regexIdx++;
                    if (regexIdx >= maxLines) {
                        // At end of headerlist
                        report.pass(file);
                        return;
                    }
                }
                if (!fileHeader[fileIdx].matches(header[regexIdx])) {
                    report.violation(file, "header", fileIdx, "Header line #%d \"%s\" does not match regex \"%s\"",
                                    fileIdx, fileHeader[fileIdx], header[regexIdx]);
                    return;
                }
            }
            report.pass(file);
        }
        catch (IOException e) {
            report.failure(file, e);
        }
    }

    private String[] readFileHeader(File file) throws IOException {
        FileReader reader = null;
        BufferedReader buf = null;
        try {
            reader = new FileReader(file);
            buf = new BufferedReader(reader);
            List<String> fileHeaders = new ArrayList<String>();
            int maxLines = header.length;

            int lineNum = 0;
            String line;
            while ((line = buf.readLine()) != null) {
                fileHeaders.add(line);
                lineNum++;
                if (lineNum >= maxLines) {
                    break;
                }
            }

            return fileHeaders.toArray(new String[0]);
        }
        finally {
            IOUtil.close(buf);
            IOUtil.close(reader);
        }
    }

}

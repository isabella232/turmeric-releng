/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.banrefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.IOUtil;

public class BannedRefChecker {
    protected static class Range {
        int start, end;
        String match;

        public Range(int idx, String word) {
            this.start = idx;
            this.end = idx + word.length();
            this.match = word;
        }

        public Range(int start, int end, String match) {
            this.start = start;
            this.end = end;
            this.match = match;
        }

        public boolean withinRange(int value) {
            return ((value >= start) && (value <= end));
        }

        public boolean overlaps(Range other) {
            return (other.withinRange(this.start) || other.withinRange(this.end) || this.withinRange(other.start) || this
                            .withinRange(other.end));
        }

        public boolean overlaps(List<Range> ranges) {
            for (Range other : ranges) {
                if (this.overlaps(other)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Range [start=");
            builder.append(start);
            builder.append(", end=");
            builder.append(end);
            builder.append(", match=");
            builder.append(match);
            builder.append("]");
            return builder.toString();
        }
    }

    private String[] included;
    private String[] excluded;

    public BannedRefChecker(String included[], String excluded[]) {
        this.included = included;
        this.excluded = excluded;
    }

    public void check(Report report, File file) {
        FileReader reader = null;
        BufferedReader buf = null;

        report.fileStart(file);
        try {
            reader = new FileReader(file);
            buf = new BufferedReader(reader);

            int linenum = 0;
            String line;
            while ((line = buf.readLine()) != null) {
                linenum++;
                testLine(report, linenum, line);
            }
        }
        catch (IOException e) {
            report.exception(e);
        }
        finally {
            IOUtil.close(buf);
            IOUtil.close(reader);
            report.fileEnd();
        }
    }

    protected void testLine(Report report, int linenum, String line) {
        List<Range> includedRanges = getRangeMatches(line, included);
        List<Range> excludedRanges = getRangeMatches(line, excluded);

        for (Range range : includedRanges) {
            if (range.overlaps(excludedRanges)) {
                continue; // Excluded: skip
            }
            report.error(linenum, range.match, range.start, range.end, line);
        }
    }

    protected List<Range> getRangeMatches(String line, String[] words) {
        List<Range> ranges = new ArrayList<Range>();
        if (words == null) {
            return ranges;
        }
        String lowerline = line.toLowerCase();
        int idx = 0;
        int end = 0;
        for (String word : words) {
            idx = lowerline.indexOf(word, idx);
            while (idx >= 0) {
                end = idx + word.length();
                ranges.add(new Range(idx, end, line.substring(idx, end)));
                idx = lowerline.indexOf(word, idx + word.length());
            }
        }
        return ranges;
    }
}

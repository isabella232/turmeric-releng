/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.banrefs.reports;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.maven.banrefs.Report;
import org.junit.Assert;

public class JunitReport implements Report {
    public static class Error {
        public Error(Throwable t) {
            linenum = 0;
            msg = String.format("%s: %s", t.getClass().getName(), t.getMessage());
        }

        public Error(int linenum2, String line, String match, int startOffset, int endOffset) {
            this.linenum = linenum2;
            this.msg = String.format("Contains Banned reference \"%s\" (offset %d) in line \"%s\"", match, startOffset,
                            line);
        }

        int linenum;
        String msg;

        @Override
        public String toString() {
            return String.format("%d) %s", linenum, msg);
        }
    }

    public static class Errors {
        List<Error> errors = new ArrayList<Error>();

        public void add(int linenum, String line, String match, int startOffset, int endOffset) {
            errors.add(new Error(linenum, line, match, startOffset, endOffset));
        }

        public void add(Throwable t) {
            errors.add(new Error(t));
        }

        public void assertCount(int expectedCount) {
            if (errors.size() != expectedCount) {
                for (Error error : errors) {
                    System.out.println(error);
                }
                Assert.assertThat("Count of errors", errors.size(), is(expectedCount));
            }
        }

        public void assertErrorExists(int linenum, String expectedString) {
            for (Error error : errors) {
                if (error.linenum == linenum) {
                    Assert.assertThat("Error Message for line " + linenum, error.msg, containsString(expectedString));
                    return;
                }
            }
            Assert.fail(String.format("Unable to find error on line number %d with string \"%s\"", linenum,
                            expectedString));
        }

    }

    private Map<File, Errors> entries = new HashMap<File, Errors>();
    private File activeFile;
    private Errors activeErrors;

    @Override
    public void close() {
        System.out.printf("JunitReport tracked %d entries%n", entries.size());
    }

    @Override
    public void fileStart(File file) {
        activeFile = file;
        activeErrors = new Errors();
    }

    @Override
    public void fileEnd() {
        entries.put(activeFile, activeErrors);
    }

    @Override
    public void exception(Throwable t) {
        activeErrors.add(t);
    }

    @Override
    public void error(int linenum, String match, int startOffset, int endOffset, String line) {
        activeErrors.add(linenum, line, match, startOffset, endOffset);
    }

    public Map<File, Errors> getEntries() {
        return entries;
    }

    public Errors assertContainsFile(File expectedFile) {
        Errors errors = entries.get(expectedFile);
        Assert.assertNotNull("Should have contained entry for: " + expectedFile, errors);
        return errors;
    }
}

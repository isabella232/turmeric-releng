/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.license.checks;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.maven.license.reports.JunitReport;
import org.junit.Assert;
import org.junit.Test;

public class JavaHeaderCheckTest {
    @Test
    public void testValid1() throws MojoExecutionException {
        assertValid("valid-java1.txt");
    }
    
    @Test
    public void testValid2() throws MojoExecutionException {
        assertValid("valid-java2.txt");
    }
    
    private void assertValid(String filename) throws MojoExecutionException {
        HeaderCheck check = new HeaderCheck("java-header.regex");

        File file = MavenTestingUtils.getTestResourceFile(filename);

        JunitReport report = new JunitReport();
        check.check(report, file);

        Assert.assertEquals("entry.count", 1, report.getEntryCount());
        JunitReport.FileEntry entry = report.getEntry(file);
        Assert.assertNotNull("Should have found entry", entry);
        System.out.println(entry);
        Assert.assertEquals("Entry.type", "none", entry.type);
    }
}

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

import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.maven.license.reports.JunitReport;
import org.junit.Assert;
import org.junit.Test;

public class HeaderCheckTest {
    @Test
    public void testOptionalHeader() throws Exception {
        HeaderCheck check = new HeaderCheck("test-optionaljava-header.regex");

        File testSrcDir = MavenTestingUtils.getProjectDir("src/test/java");
        String path = HeaderCheckTest.class.getName().replace('.', '/') + ".java";
        File srcFile = PathAssert.assertFileExists(testSrcDir, path);

        JunitReport report = new JunitReport();
        check.check(report, srcFile);

        Assert.assertEquals("entry.count", 1, report.getEntryCount());
        JunitReport.FileEntry entry = report.getEntry(srcFile);
        Assert.assertNotNull("Should have found entry", entry);
        System.out.println(entry);
        Assert.assertEquals("Entry.type", "none", entry.type);
    }
}

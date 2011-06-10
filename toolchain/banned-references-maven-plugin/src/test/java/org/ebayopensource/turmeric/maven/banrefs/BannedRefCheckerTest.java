/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.banrefs;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.List;

import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.maven.banrefs.BannedRefChecker.Range;
import org.ebayopensource.turmeric.maven.banrefs.reports.JunitReport;
import org.ebayopensource.turmeric.maven.banrefs.reports.JunitReport.Errors;
import org.junit.Assert;
import org.junit.Test;

public class BannedRefCheckerTest {
    
    @Test
    public void testGetRangeMatchesSimple() {
        String includes[] = { "fakeco" };
        String excludes[] = null;
        
        BannedRefChecker checker = new BannedRefChecker(includes, excludes);

                                                   // 012345678901234567890 
        List<Range> ranges = checker.getRangeMatches("import com.fakeco.common.Utils;", includes);
        Assert.assertNotNull("Should not have a null range", ranges);
        Assert.assertThat("Ranges.size()", ranges.size(), is(1));
        
        Range range = ranges.get(0);
        Assert.assertThat("Range.start", range.start, is(11));
        Assert.assertThat("Range.end", range.end, is(17));
        Assert.assertThat("Range.match", range.match, is("fakeco"));
    }

    @Test
    public void testGetRangeMatchesMulti() {
        String includes[] = { "fakeco" };
        String excludes[] = null;
        
        BannedRefChecker checker = new BannedRefChecker(includes, excludes);

                                                   //           1         2         3         4         5
                                                   // 012345678901234567890123456789012345678901234567890123456789 
        List<Range> ranges = checker.getRangeMatches("import com.fakeco.internal.fakecommon.FakeCoFakeColocated;", includes);
        Assert.assertNotNull("Should not have a null range", ranges);

        // @formatter:off
        Range expected[] = new Range[] {
           new Range(11, "fakeco"),
           new Range(27, "fakeco"),
           new Range(38, "FakeCo"),
           new Range(44, "FakeCo")
        };
        // @formatter:on

        Assert.assertThat("Ranges.size()", ranges.size(), is(expected.length));
        
        for(int i=0; i<expected.length; i++) {
            Range actual = ranges.get(i);
            Assert.assertThat("Range.start", actual.start, is(expected[i].start));
            Assert.assertThat("Range.end", actual.end, is(expected[i].end));
            Assert.assertThat("Range.match", actual.match, is(expected[i].match));
        }
    }

    @Test
    public void testTestLine() {
        File testSrcDir = MavenTestingUtils.getProjectDir("src/test/java");
        String path = BannedRefCheckerTest.class.getName().replace('.', '/') + ".java";
        File srcFile = PathAssert.assertFileExists(testSrcDir, path);

        String includes[] = { "fakeco" };
        String excludes[] = { "fakecoopensource", "fakeco inc", "com.fakeco.kernel" };
        
        BannedRefChecker checker = new BannedRefChecker(includes, excludes);
        JunitReport report = new JunitReport();
        report.fileStart(srcFile); // lifecycle
        
        checker.testLine(report, 1, "package com.fakeco.internal;");
        checker.testLine(report, 2, "// Copyright (c) 2010 FakeCo Inc");
        checker.testLine(report, 3, "import com.fakecoopensource.common.Utils;");
        checker.testLine(report, 4, "import com.fakeco.kernel.Logger;");
        checker.testLine(report, 5, "");
        checker.testLine(report, 6, "class FakeCoUtil { ");
        checker.testLine(report, 7, "   public static final String VERSION=Utils.asVersion(this);");
        checker.testLine(report, 8, "}");
        
        report.fileEnd(); // lifecycle
        
        Errors errors = report.assertContainsFile(srcFile);
        errors.assertCount(2);
        
        errors.assertErrorExists(1, "com.fakeco.internal");
        errors.assertErrorExists(6, "class FakeCoUtil");
    }
    
    @Test
    public void testBannedRefsSimple() {
        File testSrcDir = MavenTestingUtils.getProjectDir("src/test/java");
        String path = BannedRefCheckerTest.class.getName().replace('.', '/') + ".java";
        File srcFile = PathAssert.assertFileExists(testSrcDir, path);
        
        // @formatter:off
        String examples[] = {
            "Baseball",
            "Home Base",
            "Base Alpha",
            "BaseCall",
            "IBasedOn",
            "X-BASE-HEADER",
            "Base"
        };
        // @formatter:on

        String includes[] = { "base" };
        String excludes[] = null;
        
        BannedRefChecker checker = new BannedRefChecker(includes, excludes);
        JunitReport report = new JunitReport();
        checker.check(report, srcFile);

        Errors errors = report.assertContainsFile(srcFile);
        errors.assertCount(examples.length + includes.length);
    }
    
    @Test
    public void testBannedRefsWithExclude() {
        File testSrcDir = MavenTestingUtils.getProjectDir("src/test/java");
        String path = BannedRefCheckerTest.class.getName().replace('.', '/') + ".java";
        File srcFile = PathAssert.assertFileExists(testSrcDir, path);
        
        /* This comment is used by this test!
         * Lines that should not be found by test, due to exclusions.
         * 
         * @formatter:off
         * Copyright (c) The Man Inc
         * import com.manopensource.internal.Utils;
         * System.out.println(com.man.kernel.Logger.class.getName());
         * @formatter:on 
         */
        
        /* This comment is also used by this test!
         * Lines that should be found by the test, due to inclusions (and not present in exclusions)
         * 
         * @formatter:off
         * package com.man.old.school;
         * import com.man.external.Facade;
         * X-MAN-HEADER
         * ReponseMangler
         * RequestAlmanac
         * SomaNuance
         * ManManManManMan
         * ManualControlBean
         * @formatter:on\
         */

        String includes[] = { "man" };
        String excludes[] = { "man inc", "manopensource", "com.man.kernel" };
        
        BannedRefChecker checker = new BannedRefChecker(includes, excludes);
        JunitReport report = new JunitReport();
        checker.check(report, srcFile);

        Errors errors = report.assertContainsFile(srcFile);
        errors.assertCount(13);
    }
}

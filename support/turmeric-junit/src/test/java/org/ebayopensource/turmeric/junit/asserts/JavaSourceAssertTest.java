/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.junit.asserts;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Test;

import com.thoughtworks.qdox.model.JavaMethod;

public class JavaSourceAssertTest {

    private File getSampleDataJavaSource(String filename) {
        return MavenTestingUtils
                        .getProjectFile("src/test/java/org/ebayopensource/turmeric/junit/sampledata/"
                                        + filename);
    }

    @Test
    public void testIsInterface() throws IOException {
        File java = getSampleDataJavaSource("SampleInterface.java");
        JavaSourceAssert.assertIsInterface(java);
    }

    @Test
    public void testMethodExists() throws IOException {
        File java = getSampleDataJavaSource("SampleInterface.java");
        JavaMethod method;
        method = JavaSourceAssert.assertMethodExists(java, "public void example()");
        Assert.assertNotNull("Method", method);
        method = JavaSourceAssert.assertMethodExists(java, "public void execute(boolean flags) throws IOException");
        Assert.assertNotNull("Method", method);
    }

    @Test(expected = AssertionError.class)
    public void testMethodNotExists() throws IOException {
        File java = getSampleDataJavaSource("SampleInterface.java");
        JavaSourceAssert.assertMethodExists(java, "public void notexists()");
    }
    
    @Test
    public void testMethodExistsWithGenerics()  throws IOException {
        File java = getSampleDataJavaSource("SampleObject.java");
        JavaMethod method = JavaSourceAssert.assertMethodExists(java, "public static List<Class<?>> getImplementations() throws IOException");
        Assert.assertNotNull("Method", method);
    }
    
    @Test
    public void testMethodBodyContains() throws IOException {
        File java = getSampleDataJavaSource("SampleObject.java");
        
        JavaMethod method = JavaSourceAssert.assertMethodExists(java, "public static List<Class<?>> getImplementations() throws IOException");
        
        StringBuilder expected = new StringBuilder();
        expected.append("{ List<Class<?>> impls = new ArrayList<Class<?>>();");
        expected.append("  impls.add(SampleImplOne.class);");
        expected.append("  return impls; }");
        
        JavaSourceAssert.assertBodyContains(method, expected);
    }
    
    @Test
    public void testMethodsEqual() throws IOException {
        String signature = "public static List<Class<?>> getImplementations() throws IOException";
        File actualJava = getSampleDataJavaSource("SampleObject.java");
        File expectedJava = getSampleDataJavaSource("SampleOtherObject.java");
        
        JavaMethod actualMethod = JavaSourceAssert.assertMethodExists(actualJava, signature);
        JavaMethod expectedMethod = JavaSourceAssert.assertMethodExists(expectedJava, signature);
        
        JavaSourceAssert.assertMethodsEqual(expectedMethod, actualMethod);
    }
    
    @Test
    public void testMethodsEqualByFile() throws IOException {
        String signature = "public static List<Class<?>> getImplementations() throws IOException";
        File actualJava = getSampleDataJavaSource("SampleObject.java");
        File expectedJava = getSampleDataJavaSource("SampleOtherObject.java");
        
        JavaSourceAssert.assertMethodsEqual(expectedJava, actualJava, signature);
    }
}

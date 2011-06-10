/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.junit.asserts;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

/**
 * Assertions against Java source content.
 */
public class JavaSourceAssert {
    /**
     * Assert that the java source file is valid, parseable, and is declared as an Interface.
     * 
     * @param java
     *            the java source file.
     * @throws IOException
     */
    public static JavaClass assertIsInterface(File java) throws IOException {
        JavaDocBuilder builder = new JavaDocBuilder();
        JavaSource src = builder.addSource(java);
        JavaClass jc = getClassName(src, java);

        Assert.assertThat("Failed to parse java source: " + java.getAbsolutePath(), jc, notNullValue());

        String msg = String.format("JavaSource: (%s) should be an interface: %s", jc.getName(), java);
        Assert.assertThat(msg, jc.isInterface(), is(true));
        return jc;
    }

    /**
     * Asserts that the contents of the provided entity contains the expected contents.
     * <p>
     * The comparison is similar to {@link String#contains(CharSequence)} in so far that only part of the contents has
     * to match to be considered a success.
     * 
     * @param entity
     *            the actual java source entity to compare source against
     * @param expectedContents
     *            the expected contents.
     * @throws IOException
     */
    public static void assertBodyContains(AbstractJavaEntity entity, CharSequence expectedContents) throws IOException {
        String actualSource = codeFormat(entity.getCodeBlock());
        String expectedSource = codeFormat(expectedContents);
        Assert.assertThat(actualSource, containsString(expectedSource));
    }

    /**
     * Asserts that the contents of two methods are equal.
     * 
     * @param expectedMethod
     *            the expected java method (signature and content)
     * @param actualMethod
     *            the actual java method (signature and content)
     * 
     * @throws IOException
     */
    public static void assertMethodsEqual(JavaMethod expectedMethod, JavaMethod actualMethod) throws IOException {
        String expectedSource = codeFormat(expectedMethod);
        String actualSource = codeFormat(actualMethod);
        Assert.assertEquals(expectedSource, actualSource);
    }

    /**
     * Asserts that the contents of two methods are equal.
     * 
     * @param expectedJava
     *            the java source of the expected method
     * @param actualJava
     *            the java source of the actual method being tested
     * @param signature
     *            the method signature to lookup in both files and compare against each other.
     * 
     * @throws IOException
     */
    public static void assertMethodsEqual(File expectedJava, File actualJava, String signature) throws IOException {
        JavaMethod expectedMethod = assertMethodExists(expectedJava, signature);
        JavaMethod actualMethod = assertMethodExists(actualJava, signature);
        assertMethodsEqual(expectedMethod, actualMethod);
    }

    /**
     * Asserts that the java source file is valid, parseable, and has a constructor with provided signature (no contents
     * are checked)
     * <p>
     * Note: that any fully qualified classname present in the constructor signature will be simplified to just the
     * classname.
     * 
     * @param java
     *            the java source file
     * @param signature
     *            the constructor signature to check for.
     * @return the found JavaMethod
     * @throws IOException
     */
    public static JavaMethod assertConstructorExists(File java, CharSequence signature) throws IOException {
        JavaDocBuilder builder = new JavaDocBuilder();
        JavaSource src = builder.addSource(java);
        JavaClass jc = getClassName(src, java);

        Map<String, JavaMethod> sigs = new HashMap<String, JavaMethod>();
        for (JavaMethod method : jc.getMethods()) {
            if (method.isConstructor()) {
                sigs.put(getCompleteMethodSignature(method), method);
            }
        }

        if (!sigs.containsKey(signature)) {
            StringBuilder msg = new StringBuilder();
            msg.append("Expected constructor signature not found: ");
            msg.append(signature);
            msg.append("\nConstructors present in java source:");
            for (String sig : sigs.keySet()) {
                msg.append("\n  ").append(sig);
            }
            Assert.fail(msg.toString());
        }

        return sigs.get(signature);
    }

    /**
     * Asserts that the java source file is valid, parseable, and has the method with provided signature (no method
     * contents are checked)
     * <p>
     * Note: that any fully qualified classname present in the method signature will be simplified to just the
     * classname.
     * 
     * @param java
     *            the java source file
     * @param methodSignature
     *            the method signature to check for.
     * @return the found JavaMethod
     * @throws IOException
     */
    public static JavaMethod assertMethodExists(File java, CharSequence methodSignature) throws IOException {
        JavaDocBuilder builder = new JavaDocBuilder();
        JavaSource src = builder.addSource(java);
        JavaClass jc = getClassName(src, java);

        Map<String, JavaMethod> sigs = new HashMap<String, JavaMethod>();
        for (JavaMethod method : jc.getMethods()) {
            if (!method.isConstructor()) {
                sigs.put(getCompleteMethodSignature(method), method);
            }
        }

        if (!sigs.containsKey(methodSignature)) {
            StringBuilder msg = new StringBuilder();
            msg.append("Expected method signature not found: ");
            msg.append(methodSignature);
            msg.append("\nMethods present in java source:");
            for (String sig : sigs.keySet()) {
                msg.append("\n  ").append(sig);
            }
            Assert.fail(msg.toString());
        }

        return sigs.get(methodSignature);
    }

    private static String codeFormat(JavaMethod method) throws IOException {
        StringBuilder raw = new StringBuilder();
        raw.append(getCompleteMethodSignature(method));
        raw.append(" {\n");
        raw.append(method.getSourceCode());
        raw.append("}");
        return codeFormat(raw.toString());
    }

    /**
     * *VERY* basic code formatter, to make code comparison more consistent.
     * 
     * @throws IOException
     */
    private static String codeFormat(CharSequence rawcode) throws IOException {
        StringBuilder ret = new StringBuilder();

        Scanner scanner = new Scanner(new StringReader(rawcode.toString()));
        boolean needsIndent = false;
        String tok;
        String indent = "";
        while (scanner.hasNext()) {
            tok = scanner.next();
            if (tok.equals("{")) {
                indent += "    ";
                ret.append("{\n").append(indent);
            }
            else if (tok.endsWith(";")) {
                if (needsIndent) {
                    ret.append(indent);
                    needsIndent = false;
                }
                ret.append(tok).append("\n");
                needsIndent = true;
            }
            else if (tok.equals("}")) {
                indent = indent.substring(4);
                ret.append(indent).append("}\n");
            }
            else {
                if (needsIndent) {
                    ret.append(indent);
                    needsIndent = false;
                }
                ret.append(tok).append(' ');
            }
        }

        return ret.toString();
    }

    private static JavaClass getClassName(JavaSource src, File java) {
        JavaClass classes[] = src.getClasses();
        String classname = java.getName();
        classname = classname.substring(0, classname.length() - ".java".length());
        for (JavaClass jc : classes) {
            if (jc.getName().equals(classname)) {
                return jc;
            }
        }
        return null;
    }

    private static String getCompleteMethodSignature(JavaMethod method) {
        StringBuilder ret = new StringBuilder();

        // Accessibility Modifiers
        for (String modifier : method.getModifiers()) {
            if (modifier.startsWith("p")) {
                ret.append(modifier);
                ret.append(' ');
            }
        }

        // Non Accessibility Modifiers
        for (String modifier : method.getModifiers()) {
            if (!modifier.startsWith("p")) {
                ret.append(modifier);
                ret.append(' ');
            }
        }

        // Returns
        if (!method.isConstructor()) {
            appendGeneric(ret, method.getReturnType());
            ret.append(' ');
        }

        // Name
        ret.append(method.getName());

        // Parameters
        ret.append('(');
        boolean needsDelim = false;
        for (JavaParameter parameter : method.getParameters()) {
            if (needsDelim) {
                ret.append(", ");
            }
            appendGeneric(ret, parameter.getType());
            if (parameter.isVarArgs()) {
                ret.append("...");
            }
            ret.append(' ');
            ret.append(parameter.getName());
            needsDelim = true;
        }

        ret.append(')');

        // Throws
        if (method.getExceptions().length > 0) {
            ret.append(" throws ");
            needsDelim = false;
            for (Type exception : method.getExceptions()) {
                if (needsDelim) {
                    ret.append(", ");
                }
                appendGeneric(ret, exception);
            }
        }

        return ret.toString();
    }

    private static void appendGeneric(StringBuilder buf, Type type) {
        String fqn = type.getFullyQualifiedName();
        int lastDot = fqn.lastIndexOf('.');
        int lastDollar = fqn.lastIndexOf('$');
        if (lastDot > 0 && lastDollar > 0) {
            int split = Math.min(lastDot, lastDollar);
            buf.append(fqn.substring(split + 1));
        }
        else if (lastDot > 0) {
            buf.append(fqn.substring(lastDot + 1));
        }
        else if (lastDollar > 0) {
            buf.append(fqn.substring(lastDollar + 1));
        }
        else {
            buf.append(fqn);
        }
        Type args[] = type.getActualTypeArguments();
        if ((args != null) && (args.length > 0)) {
            buf.append('<');
            boolean needsDelim = false;
            for (Type arg : args) {
                if (needsDelim) {
                    buf.append(',');
                }
                appendGeneric(buf, arg);
                needsDelim = true;
            }
            buf.append('>');
        }
        for (int dim = 0; dim < type.getDimensions(); dim++) {
            buf.append("[]");
        }
    }
}

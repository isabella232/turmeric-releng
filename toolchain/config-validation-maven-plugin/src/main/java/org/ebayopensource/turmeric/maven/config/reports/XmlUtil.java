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

public class XmlUtil {
    public static String escaped(Class<?> clazz) {
        return escaped(clazz.getName());
    }

    public static String escaped(File path) {
        return escaped(path.getAbsolutePath());
    }

    public static String escaped(String raw) {
        StringBuilder ret = new StringBuilder();
        for (char c : raw.toCharArray()) {
            switch (c) {
                case '\"':
                    ret.append("&quot;");
                    break;
                case '\'':
                    ret.append("&apos;");
                    break;
                case '<':
                    ret.append("&lt;");
                    break;
                case '>':
                    ret.append("&gt;");
                    break;
                case '&':
                    ret.append("&amp;");
                    break;
                default:
                    ret.append(c);
                    break;
            }
        }
        return ret.toString();
    }

    public static String escaped(String format, Object... args) {
        return escaped(String.format(format, args));
    }
}

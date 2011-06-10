/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.license.util;

public class JunitConsole implements Console {
    public static final Console INSTANCE = new JunitConsole();

    @Override
    public void printf(String format, Object... args) {
        System.out.println("[CONSOLE] " + String.format(format, args));
    }

    @Override
    public void println(Throwable t) {
        System.out.print("[CONSOLE] ");
        t.printStackTrace(System.out);
    }
}

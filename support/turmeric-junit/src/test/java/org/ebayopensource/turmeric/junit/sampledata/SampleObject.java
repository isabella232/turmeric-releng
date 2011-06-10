/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.junit.sampledata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SampleObject {

    public static List<Class<?>> getImplementations() throws IOException {
        List<Class<?>> impls = new ArrayList<Class<?>>();
        impls.add(SampleImplOne.class);
        return impls;
    }

    @SuppressWarnings("unused")
    private Map<String, List<? extends Object>> getMatches(Set<String> possibles) {
        Map<String, List<? extends Object>> ret = new HashMap<String, List<? extends Object>>();
        /* do nothing here */
        return ret;
    }
}

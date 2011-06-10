/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.banrefs;

import java.io.File;

public interface Report {

    void close();

    void fileStart(File file);

    void fileEnd();

    void exception(Throwable t);

    void error(int linenum, String match, int startOffset, int endOffset, String line);

}

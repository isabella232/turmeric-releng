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
import org.ebayopensource.turmeric.maven.license.Checker;
import org.ebayopensource.turmeric.maven.license.Report;

public class XmlHeaderCheck implements Checker {
    private HeaderCheck header;

    public XmlHeaderCheck() throws MojoExecutionException {
        header = new HeaderCheck("xml-header.regex");
    }

    @Override
    public void check(Report report, File file) {
        header.check(report, file);
    }
}

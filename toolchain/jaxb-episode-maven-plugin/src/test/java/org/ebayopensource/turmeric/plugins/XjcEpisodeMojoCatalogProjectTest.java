/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins;

import java.io.File;

import org.junit.Ignore;



/**
 * Test the basic code gen options.
 */
 @Ignore("Not ready.")
public class XjcEpisodeMojoCatalogProjectTest extends BaseXjcEpisodeMojoTestCase {

	@Override
	public String getTestMojoDirName() {
		return "CatalogProject";
	}
	
	public void testOptionsBasic() throws Exception {
		setMojoLoggingDebug(true);
		
		XjcEpisodeMojo mojo = createMojo();
		executeMojo(mojo);

		File xsdFile = mojo.getXsdEpisodeFile();
		assertTrue("Unable to find properly configured xsdSourceFile: "
				+ xsdFile, xsdFile.exists());
	}
}

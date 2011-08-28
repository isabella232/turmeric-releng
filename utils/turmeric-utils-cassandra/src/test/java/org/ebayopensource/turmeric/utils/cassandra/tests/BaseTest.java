/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.tests;

import org.ebayopensource.turmeric.utils.cassandra.server.CassandraTestManager;

/**
 * The Class BaseTest.
 * @author jamuguerza
 */
public abstract class BaseTest {
	
	/** The Constant TURMERIC_TEST_CLUSTER. */
	protected static final String TURMERIC_TEST_CLUSTER = "TestCluster";
	
	/** The Constant KEY_SPACE. */
	protected static final String KEY_SPACE = "TestKeyspace";
	
	/** The Constant HOST. */
	protected static final String HOST = "127.0.1.10:9160";
}

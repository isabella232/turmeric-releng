/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.ebayopensource.turmeric.utils.cassandra.BootStrap;
import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao;
import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDaoImpl;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class ModelDaoTest.
 * @author jamuguerza
 */
public class ModelDaoTest extends BaseTest {
	
	/** The test model dao. */
	private ModelDao testModelDao;
	

	/**
	 * Before.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void before() throws Exception {
		    BootStrap.init();
		    testModelDao  = new ModelDaoImpl(TURMERIC_TEST_CLUSTER, HOST, KEY_SPACE, "TestCF");
			
	}

	 //@After
	  /**
 	 * After class.
 	 *
 	 * @throws Exception the exception
 	 */
 	public static void afterClass() throws Exception {
	    if (server != null) {
	      server.teardown();
	    }
	 }
	 

	/** The KEY. */
	private static String KEY = "testModel_002";

	/**
	 * Life cycle.
	 */
	@Test
	public void lifeCycle() {
		Model testModel = new Model();
		testModel.setKey(KEY);
		testModel.setBooleanData(Boolean.TRUE);
		testModel.setIntData(Integer.MAX_VALUE);
		testModel.setLongData(Long.MAX_VALUE);
		testModel.setStringData("any String");
		testModel.setTimeData(new Date(System.currentTimeMillis()));

		
		assertTrue(testModelDao.find(KEY) == null);
		
		testModelDao.save(testModel);
		testModel = testModelDao.find(KEY);
		assertNotNull(testModel);

		testModelDao.delete(testModel);
		testModel = testModelDao.find(KEY);
		assertTrue(testModel == null);
	}
}

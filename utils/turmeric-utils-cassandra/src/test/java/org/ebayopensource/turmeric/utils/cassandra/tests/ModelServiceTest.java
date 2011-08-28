/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.ebayopensource.turmeric.utils.cassandra.server.CassandraTestManager;
import org.ebayopensource.turmeric.utils.cassandra.service.ModelService;
import org.ebayopensource.turmeric.utils.cassandra.service.ModelServiceImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class ModelServiceTest.
 * @author jamuguerza
 */
public class ModelServiceTest extends BaseTest {
	
	/** The test model service. */
	private ModelService testModelService;
	
	/** The KEY. */
	private static String KEY = "testModel_002";

	/**
	 * Before.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void before() throws Exception {
		CassandraTestManager.initialize();
		testModelService  = new ModelServiceImpl(TURMERIC_TEST_CLUSTER, HOST, KEY_SPACE,  "TestCF");	
	}


	/**
	 * Life cycle.
	 */
	@Test
	public void testSave() {
			Model testModel = createModel();

		//not stored yet
		assertFalse(testModelService.containsModel(KEY));

		//save
		testModelService.createTestModel(testModel);
		//find
		testModel = testModelService.getTestModel(KEY);
		assertNotNull(testModel);

		//contains
		assertTrue(testModelService.containsModel(KEY));
		
		//delete
		testModelService.deleteTestModel(testModel);
		testModel = testModelService.getTestModel(KEY);
		assertTrue(testModel == null);
		
//		gelAllKeys
		 testModel = createModel();
		 
		 for (int i = 1; i < 20; i++) {
			 testModel.setKey(KEY + i);
			 testModelService.createTestModel(testModel);
		 }
		 Set<String> allKeys = testModelService.getAllKeys();
		 
		 assertEquals(20, allKeys.size());
		 assertTrue(allKeys.contains(testModel.getKey()));
	}
	
	private Model createModel() {
		Model testModel = new Model();
		testModel.setKey(KEY);
		testModel.setBooleanData(Boolean.TRUE);
		testModel.setIntData(Integer.MAX_VALUE);
		testModel.setLongData(Long.MAX_VALUE);
		testModel.setStringData("any String");
		testModel.setTimeData(new Date(System.currentTimeMillis()));
		return testModel;
	}

}

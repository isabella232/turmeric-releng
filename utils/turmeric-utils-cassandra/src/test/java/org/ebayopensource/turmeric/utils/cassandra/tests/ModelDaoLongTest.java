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

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao;
import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDaoImpl;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.ebayopensource.turmeric.utils.cassandra.server.CassandraTestManager;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class ModelDaoTest.
 * 
 * @author jamuguerza
 */
public class ModelDaoLongTest extends BaseTest {

	/** The test model dao. */
	private static ModelDao testModelDao;

	/** The KEY. */
	private static Long KEY = 100L;

	/**
	 * Before.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		CassandraTestManager.initialize();
		testModelDao = new ModelDaoImpl(TURMERIC_TEST_CLUSTER, HOST, KEY_SPACE,
				"TestLongCF", Long.class);
	}

	@After
	public void after() throws Exception {
		for (Object key : testModelDao.getAllKeys()) {
			Model model = new Model(0L);
			model.setKey(key);
			testModelDao.delete(model);
		}
		Thread.sleep(1000);
	}

	@Test
	public void testSave() {
		Model testModel = createModel();
		testModel.setKey(KEY + 1);

		// save
		testModelDao.save(testModel);

		//Contains
		assertTrue(testModelDao.containsKey(KEY + 1));

		// find
		testModel = testModelDao.find(KEY+ 1);
		assertNotNull(testModel);
	}

	@Test
	public void testContains() {
		Model testModel = createModel();
		testModel.setKey(KEY + 2L);
		testModelDao.save(testModel);

		// contains
		assertTrue(testModelDao.containsKey(KEY + 2L));
		assertFalse(testModelDao.containsKey(KEY + 111111L));
		assertEquals(1, testModelDao.getAllKeys().size());
	}

	@Test
	public void testDelete() {
		Model testModel = createModel();
		testModel.setKey(KEY);
		// save
		testModelDao.save(testModel);

		// find
		testModel = testModelDao.find(KEY);
		assertNotNull(testModel);

		// delete
		testModelDao.delete(testModel);
		assertFalse(testModelDao.containsKey(KEY));
		testModel = testModelDao.find(KEY);
		assertTrue(testModel == null);
	}

	@Test
	public void testGetAllKeys() {

		Model testModel = createModel();

		// save
		for (int i = 0; i < 20; i++) {
			testModel.setKey(KEY + i);
			testModelDao.save(testModel);
		}

		// gelAllKeys
		Set<String> allKeys = testModelDao.getAllKeys();

		assertEquals(20, allKeys.size());
		assertTrue(allKeys.contains((Long)testModel.getKey()));
	}

	@Test
	public void testFindItems() {

		// findItems
		ArrayList<Long> keyList = new ArrayList<Long>();
		keyList.add(1000L);
		keyList.add(1001L);
		keyList.add(1002L);

		Model testModel = createModel();
		testModel.setKey(keyList.get(0));
		Model testModel1 = createModel();
		testModel1.setKey(keyList.get(1));
		Model testModel2 = createModel();
		testModel2.setKey(keyList.get(2));

		// save
		testModelDao.save(testModel);
		testModelDao.save(testModel1);
		testModelDao.save(testModel2);

		Set<Model> result = testModelDao.findItems(keyList, "", "");
		assertNotNull(result);
		assertEquals(3, result.size());

	}

	private Model createModel() {
		Model testModel = new Model(0L);
		testModel.setKey(KEY);
		testModel.setBooleanData(Boolean.TRUE);
		testModel.setIntData(Integer.MAX_VALUE);
		testModel.setLongData(Long.MAX_VALUE);
		testModel.setStringData("any String");
		testModel.setTimeData(new Date(System.currentTimeMillis()));
		return testModel;
	}

}

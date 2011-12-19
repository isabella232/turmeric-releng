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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.dao.AbstractColumnFamilyDao;
import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao;
import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDaoImpl;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.ebayopensource.turmeric.utils.cassandra.model.ModelWithDefaultAndMultiParamConstructor;
import org.ebayopensource.turmeric.utils.cassandra.model.ModelWithMultipleParamConstructor;
import org.ebayopensource.turmeric.utils.cassandra.server.CassandraTestManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class ModelDaoTest.
 * 
 * @author jamuguerza
 */
public class ModelDaoStringTest extends BaseTest {

	public static class AnotherModelDaoImpl extends
			AbstractColumnFamilyDao<String, ModelWithMultipleParamConstructor> {

		public AnotherModelDaoImpl() {
			super(TURMERIC_TEST_CLUSTER, HOST, KEY_SPACE, String.class,
					ModelWithMultipleParamConstructor.class,
					"TestAnotherStringCF");

		}
	};

	public static class YetAnotherModelDaoImpl
			extends
			AbstractColumnFamilyDao<String, ModelWithDefaultAndMultiParamConstructor> {

		public YetAnotherModelDaoImpl() {
			super(TURMERIC_TEST_CLUSTER, HOST, KEY_SPACE, String.class,
					ModelWithDefaultAndMultiParamConstructor.class,
					"TestYetAnotherStringCF");

		}
	};

	/** The test model dao. */
	private static ModelDao testModelDao;

	/** The KEY. */
	private String KEY = null;

	private Map columns;

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
				"TestStringCF", String.class);
	}

	@Before
	public void setUp() {
		columns = new HashMap<String, String>();
		columns.put("coulmn1", "value1");
		KEY = "testModel_" + Math.random();
	}

	@After
	public void after() throws Exception {
		for (Object key : testModelDao.getAllKeys()) {
			Model model = new Model("");
			model.setKey(key);
			testModelDao.delete(model);
		}
		Thread.sleep(1000);
	}

	@Test
	public void testSave() {
		Model testModel = createModel();
		testModel.setKey(KEY + "test_save");

		// save
		testModelDao.save(testModel);

		// Contains
		assertTrue(testModelDao.containsKey(KEY + "test_save"));

		// find
		testModel = testModelDao.find(KEY + "test_save");
		assertNotNull(testModel);
	}

	@Test
	public void testContains() {
		Model testModel = createModel();
		testModel.setKey(KEY + "test_contains");
		testModelDao.save(testModel);

		// contains
		assertTrue(testModelDao.containsKey(KEY + "test_contains"));
		assertFalse(testModelDao.containsKey(KEY + "111111"));
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
		assertTrue(allKeys.contains(testModel.getKey()));
	}

	@Test
	public void testFindItems() {

		// findItems
		ArrayList<String> keyList = new ArrayList<String>();
		keyList.add("findItem_001");
		keyList.add("findItem_002");
		keyList.add("findItem_003");

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

	@Test
	public void testFind() {
		Model testModel = createModel();
		columns = new HashMap<String, String>();
		columns.put("coulmn1", "value1");
		testModel.setColumns(columns);
		testModelDao.save(testModel);

		Model result = testModelDao.find(KEY);
		assertNotNull(result);
		assertEquals(KEY, result.getKey());
		assertNotNull(result.getColumns());
		assertEquals(1, result.getColumns().size());
		assertEquals(columns, result.getColumns());
	}

	@Test(expected = RuntimeException.class)
	public void testFindModelConstructorWithMultipleParams() {
		ModelWithMultipleParamConstructor<String> theModelObj = new ModelWithMultipleParamConstructor<String>(
				"X", 2l, 2.3f);
		theModelObj.setColumns(columns);
		theModelObj.setKey(KEY);
		AbstractColumnFamilyDao anotherDao = new AnotherModelDaoImpl();
		anotherDao.save(KEY, theModelObj);

		ModelWithMultipleParamConstructor result = (ModelWithMultipleParamConstructor) anotherDao
				.find(KEY);
		fail("The test should fail because there is no suitable constructor in ModelWithMultipleParamConstructor.class");
	}

	@Test
	public void testFindModelWithDefaultAndMultiParamConstructor() {
		ModelWithDefaultAndMultiParamConstructor<String> theModelObj = new ModelWithDefaultAndMultiParamConstructor<String>(
				"X", 2l, 2.3f);
		theModelObj.setColumns(columns);
		theModelObj.setKey(KEY);
		YetAnotherModelDaoImpl yetAnotherDao = new YetAnotherModelDaoImpl();
		yetAnotherDao.save(KEY, theModelObj);

		ModelWithDefaultAndMultiParamConstructor result = yetAnotherDao
				.find(KEY);
		assertNotNull(result);
		assertEquals(KEY, result.getKey());
		assertNotNull(result.getColumns());
		assertEquals(columns, result.getColumns());
	}

	private Model createModel() {
		Model testModel = new Model("");
		testModel.setKey(KEY);
		testModel.setColumns(columns);
		return testModel;
	}

}

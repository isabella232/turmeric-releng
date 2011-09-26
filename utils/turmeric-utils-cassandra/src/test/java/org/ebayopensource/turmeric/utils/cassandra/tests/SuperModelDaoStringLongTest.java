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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.dao.SuperModelDao;
import org.ebayopensource.turmeric.utils.cassandra.dao.SuperModelDaoImpl;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.ebayopensource.turmeric.utils.cassandra.model.SuperModel;
import org.ebayopensource.turmeric.utils.cassandra.server.CassandraTestManager;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class SuperModelDaoStringStringTest.
 * 
 * @author jamuguerza
 */
public class SuperModelDaoStringLongTest extends BaseTest {

	/** The test Super model dao. */
	private static SuperModelDao testSuperModelDao;

	/** The SUPER_KEY. */
	private static String SUPER_KEY = "superKey_001";

	/** The KEY. */
	private static Long KEY = 1L;

	/**
	 * Before.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		CassandraTestManager.initialize();
		testSuperModelDao = new SuperModelDaoImpl(TURMERIC_TEST_CLUSTER, HOST, KEY_SPACE,
				"TestSuperStringLongCF", String.class, Long.class);
	}

	@After
	public void after() throws Exception {
		for (Object superKey : testSuperModelDao.getAllKeys()) {
			SuperModel superModel = new SuperModel("",0L);
			superModel.setKey(superKey);
			testSuperModelDao.delete(superModel);
		}
		Thread.sleep(1000);
	}

	@Test
	public void testSave() {
		SuperModel testSuperModel = createSuperModel();

		// save
		testSuperModelDao.save(testSuperModel);

		// find
		Long [] columnNames = new Long [] {100L, 101L};  

		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue( testSuperModel.getColumns().containsKey(100L));
		assertTrue(testSuperModel.getColumns().containsKey(101L));
		
		assertNotNull(testSuperModel);
	}

	@Test
	public void testContainsKey() {
		SuperModel testSuperModel = createSuperModel();
		assertFalse(testSuperModelDao.containsKey(SUPER_KEY));
		// save
		testSuperModelDao.save(testSuperModel);
		assertTrue(testSuperModelDao.containsKey(SUPER_KEY));
	
		testSuperModelDao.delete(testSuperModel);
		assertFalse(testSuperModelDao.containsKey(SUPER_KEY));
		
	}
		
	@Test
	public void testFind() {
		SuperModel testSuperModel = createSuperModel();

		// save
		testSuperModelDao.save(testSuperModel);

//		// find
		Long [] columnNames = null;  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(3, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey(100L));
		assertTrue(testSuperModel.getColumns().containsKey(101L));
		assertTrue(testSuperModel.getColumns().containsKey(102L));

		columnNames = new Long [] {100L, 101L};  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey(100L));
		assertTrue(testSuperModel.getColumns().containsKey(101L));
		assertFalse(testSuperModel.getColumns().containsKey(102L));
	
		//non consecutive
		columnNames = new Long [] {100L,102L};  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey(100L));
		assertFalse(testSuperModel.getColumns().containsKey(101L));
		assertTrue(testSuperModel.getColumns().containsKey(102L));
	
		// non ordered
		columnNames = new Long [] {102L, 100L};  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey(100L));
		assertFalse(testSuperModel.getColumns().containsKey(101L));
		assertTrue(testSuperModel.getColumns().containsKey(102L));
				
	}

	@Test
	public void testDelete() {
		SuperModel testSuperModel = createSuperModel();
		testSuperModel.setKey(SUPER_KEY);
		// save
		testSuperModelDao.save(testSuperModel);
		
		// find
		Long [] columnNames = new Long[] {101L, 102L};  
		testSuperModel = testSuperModelDao.find(SUPER_KEY , columnNames);
		assertNotNull(testSuperModel);

		// delete
		testSuperModelDao.delete(testSuperModel);
		assertFalse(testSuperModelDao.containsKey(SUPER_KEY));
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNull(testSuperModel);
	}

	@Test
	public void testGetAllKeys() {

		SuperModel testSuperModel = createSuperModel();

		// save
		for (int i = 0; i < 20; i++) {
			testSuperModel.setKey(SUPER_KEY + i);
			testSuperModelDao.save(testSuperModel);
		}

		// gelAllKeys
		Set<String> allKeys = testSuperModelDao.getAllKeys();

		assertEquals(20, allKeys.size());
		assertTrue(allKeys.contains(testSuperModel.getKey()));
	}

	@Test
	public void testMultipleItems() {

		// findItems
		ArrayList<String> superKeyList = new ArrayList<String>();
		superKeyList.add(SUPER_KEY + "findItem_001");
		superKeyList.add(SUPER_KEY + "findItem_002");
		superKeyList.add(SUPER_KEY + "findItem_003");

		SuperModel testSuperModel0 = createSuperModel();
		testSuperModel0.setKey(superKeyList.get(0));
		SuperModel testSuperModel1 = createSuperModel();
		testSuperModel1.setKey(superKeyList.get(1));
		SuperModel testSuperModel2 = createSuperModel();
		testSuperModel2.setKey(superKeyList.get(2));

		// save
		testSuperModelDao.save(testSuperModel0);
		testSuperModelDao.save(testSuperModel1);
		testSuperModelDao.save(testSuperModel2);

		 Map<String, SuperModel> result = testSuperModelDao.findItems(superKeyList, new Long[] {100L, 102L});
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(2, result.get(SUPER_KEY + "findItem_001").getColumns().size());

		assertTrue( result.containsKey(SUPER_KEY + "findItem_002"));
		
		Map<String, Model> columns = result.get(SUPER_KEY + "findItem_001").getColumns();
		assertEquals(2, result.get(SUPER_KEY + "findItem_001").getColumns().size());

	}

	private SuperModel createSuperModel() {
		SuperModel testSuperModel = new SuperModel("", 0L );
		
		Model model1 = createModel();
		model1.setKey(100L);
		Model model2 = createModel();
		model2.setKey(101L);
		Model model3 = createModel();
		model3.setKey(102L);
		
		HashMap<Long, Model> columns = new HashMap<Long, Model>();
		columns.put((Long)model1.getKey(), model1);
		columns.put((Long)model2.getKey(), model2);
		columns.put((Long)model3.getKey(), model3);
		
		testSuperModel.setKey(SUPER_KEY);
		testSuperModel.setColumns(columns);

		return testSuperModel;
	}
	
	private Model createModel() {
		Model testModel = new Model(0L);
		testModel.setKey(KEY);
		Map columns = new HashMap<String, String>();
		columns.put("coulmn1","value1");
		testModel.setColumns(columns);
		
		return testModel;
	}

}

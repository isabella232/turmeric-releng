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
import java.util.List;
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
public class SuperModelDaoStringStringTest extends BaseTest {

	/** The test Super model dao. */
	private static SuperModelDao testSuperModelDao;

	/** The SUPER_KEY. */
	private static String SUPER_KEY = "superKey_001";

	/** The KEY. */
	private static String KEY = "key_aaa01";

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
				"TestSuperStringStringCF", String.class, String.class);
	}

	@After
	public void after() throws Exception {
		for (Object superKey : testSuperModelDao.getAllKeys()) {
			SuperModel superModel = new SuperModel("","");
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
		String [] columnNames = new String [] {"key_aaa01model1", "key_aaa01model2"};  

		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue( testSuperModel.getColumns().containsKey("key_aaa01model1"));
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model2"));
		
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

		// find
		String [] columnNames = null;  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(3, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model1"));
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model2"));
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model3"));

		columnNames = new String [] {"key_aaa01model1", "key_aaa01model2"};  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model1"));
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model2"));
		assertFalse(testSuperModel.getColumns().containsKey("key_aaa01model3"));
	
		//non consecutive
		columnNames = new String [] {"key_aaa01model1", "key_aaa01model3"};  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model1"));
		assertFalse(testSuperModel.getColumns().containsKey("key_aaa01model2"));
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model3"));
	
		// non ordered
		columnNames = new String [] {"key_aaa01model3", "key_aaa01model1"};  
		testSuperModel = testSuperModelDao.find(SUPER_KEY, columnNames);
		assertNotNull(testSuperModel);
		assertEquals(2, testSuperModel.getColumns().size());
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model1"));
		assertFalse(testSuperModel.getColumns().containsKey("key_aaa01model2"));
		assertTrue(testSuperModel.getColumns().containsKey("key_aaa01model3"));
				
	}

	@Test
	public void testFindByColumnRange() {
		List<SuperModel> superModelList = new ArrayList<SuperModel>();
		SuperModel testSuperModel = createSuperModel();
		SuperModel testSuperModel1 = createSuperModel_1();
		SuperModel testSuperModel2 = createSuperModel_2();

		// save
		testSuperModelDao.save(testSuperModel);
		testSuperModelDao.save(testSuperModel1);
		testSuperModelDao.save(testSuperModel2);
		
		// find
		superModelList = testSuperModelDao.findByRange( "key_aaa01model1", "key_aaa01model2");
		assertNotNull(superModelList);
		assertEquals(3, superModelList.size());
		assertEquals(2, superModelList.get(0).getColumns().size());
		assertEquals(1, superModelList.get(1).getColumns().size());
		assertEquals(1, superModelList.get(2).getColumns().size());
		
		assertTrue(superModelList.get(0).getColumns().containsKey("key_aaa01model1"));
		assertTrue(superModelList.get(0).getColumns().containsKey("key_aaa01model2"));
		assertFalse(superModelList.get(0).getColumns().containsKey("key_aaa01model3"));
		
		assertFalse(superModelList.get(1).getColumns().containsKey("key_aaa01model1"));
		assertTrue(superModelList.get(1).getColumns().containsKey("key_aaa01model2"));
		assertFalse(superModelList.get(1).getColumns().containsKey("key_aaa01model3"));
		
		assertTrue(superModelList.get(2).getColumns().containsKey("key_aaa01model1"));
		assertFalse(superModelList.get(2).getColumns().containsKey("key_aaa01model2"));
		assertFalse(superModelList.get(2).getColumns().containsKey("key_aaa01model3"));
		
		
		// find2
		superModelList = testSuperModelDao.findByRange( "key_aaa01model4", "key_aaa01model5");
		assertNotNull(superModelList);
		assertEquals(2, superModelList.size());
		assertEquals(1, superModelList.get(0).getColumns().size());
		assertEquals(2, superModelList.get(1).getColumns().size());
		
		
		assertTrue(superModelList.get(0).getColumns().containsKey("key_aaa01model4"));
		assertFalse(superModelList.get(0).getColumns().containsKey("key_aaa01model5"));
		
		assertTrue(superModelList.get(1).getColumns().containsKey("key_aaa01model4"));
		assertTrue(superModelList.get(1).getColumns().containsKey("key_aaa01model5"));
					
	}
	
	@Test
	public void testDelete() {
		SuperModel testSuperModel = createSuperModel();
		testSuperModel.setKey(SUPER_KEY);
		// save
		testSuperModelDao.save(testSuperModel);
		
		// find
		String [] columnNames = new String [] {"key_aaa01model1", "key_aaa01model2"};  
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

		 Map<String, SuperModel> result = testSuperModelDao.findItems(superKeyList, new String[] {KEY+ "model1", KEY+ "model3"});
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(2, result.get(SUPER_KEY + "findItem_001").getColumns().size());

		assertTrue( result.containsKey(SUPER_KEY + "findItem_002"));
		
		Map<String, Model> columns = result.get(SUPER_KEY + "findItem_001").getColumns();
		assertEquals(2, result.get(SUPER_KEY + "findItem_001").getColumns().size());

	}

	private SuperModel createSuperModel() {
		SuperModel testSuperModel = new SuperModel("", "" );
		
		Model model1 = createModel();
		model1.setKey(model1.getKey()  + "model1");
		Model model2 = createModel();
		model2.setKey(model2.getKey() + "model2");
		Model model3 = createModel();
		model3.setKey(model3.getKey()  + "model3");
		
		HashMap<String, Model> columns = new HashMap<String, Model>();
		columns.put((String)model1.getKey(), model1);
		columns.put((String)model2.getKey(), model2);
		columns.put((String)model3.getKey(), model3);
		
		testSuperModel.setKey(SUPER_KEY);
		testSuperModel.setColumns(columns);

		return testSuperModel;
	}
	
	private SuperModel createSuperModel_1() {
		SuperModel testSuperModel = new SuperModel("", "" );
		
		Model model1 = createModel();
		model1.setKey(model1.getKey()  + "model2");
		Model model2 = createModel();
		model2.setKey(model2.getKey() + "model3");
		Model model3 = createModel();
		model3.setKey(model3.getKey()  + "model4");
		
		HashMap<String, Model> columns = new HashMap<String, Model>();
		columns.put((String)model1.getKey(), model1);
		columns.put((String)model2.getKey(), model2);
		columns.put((String)model3.getKey(), model3);
		
		testSuperModel.setKey(SUPER_KEY + 1);
		testSuperModel.setColumns(columns);

		return testSuperModel;
	}
	
	private SuperModel createSuperModel_2() {
		SuperModel testSuperModel = new SuperModel("", "" );
		
		Model model1 = createModel();
		model1.setKey(model1.getKey()  + "model5");
		Model model2 = createModel();
		model2.setKey(model2.getKey() + "model6");
		Model model3 = createModel();
		model3.setKey(model3.getKey()  + "model1");
		Model model4 = createModel();
		model4.setKey(model4.getKey()  + "model4");
		
		HashMap<String, Model> columns = new HashMap<String, Model>();
		columns.put((String)model1.getKey(), model1);
		columns.put((String)model2.getKey(), model2);
		columns.put((String)model3.getKey(), model3);
		columns.put((String)model4.getKey(), model4);
		
		testSuperModel.setKey(SUPER_KEY + 2 );
		testSuperModel.setColumns(columns);

		return testSuperModel;
	} 
	
	
	private Model createModel() {
		Model testModel = new Model("");
		testModel.setKey(KEY);
		Map columns = new HashMap<String, String>();
		columns.put("coulmn1","value1");
		testModel.setColumns(columns);
		
		return testModel;
	}

}

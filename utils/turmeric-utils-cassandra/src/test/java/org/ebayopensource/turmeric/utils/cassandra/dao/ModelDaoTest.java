package org.ebayopensource.turmeric.utils.cassandra.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.junit.Before;
import org.junit.Test;

public class ModelDaoTest extends AbstractDao {
	private ModelDao testModelDao;

	@Before
	public void before() {
		super.before();
		testModelDao = new ModelDaoImpl(TURMERIC_TEST_CLUSTER, HOST, keySpace);
	}

	private static String KEY = "testModel_001";

	@Test
	public void lifeCycle() {
		Model testModel = new Model();
		testModel.setKey(KEY);
		testModel.setBooleanData(Boolean.TRUE);
		testModel.setIntData(Integer.MAX_VALUE);
		testModel.setLongData(Long.MAX_VALUE);
		testModel.setStringData("any String");
		testModel.setTimeData(new Date(System.currentTimeMillis()));

		testModelDao.save(testModel);
		testModel = testModelDao.find(KEY);
		assertNotNull(testModel);

		testModelDao.delete(testModel);
		testModel = testModelDao.find(KEY);
		assertTrue(testModel == null);
	}
}

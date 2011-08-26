package org.ebayopensource.turmeric.utils.cassandra.service;

import java.util.Set;

import me.prettyprint.hector.api.Keyspace;

import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao;
import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDaoImpl;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;

public class CassandraTestServiceImpl implements CassandraTestService {
	  private final ModelDao testModelDao;
	
	 public CassandraTestServiceImpl(final String clusterName, final String host, final Keyspace keySpace) {
		    testModelDao = new ModelDaoImpl(clusterName, host, keySpace);
		  }
	 
	@Override
	public Model getTestModel(String key) {
		return testModelDao.find(key);
	}

	@Override
	public void deleteTestModel(Model testModel) {
		testModelDao.delete(testModel);
	}

	@Override
	public Set<String> getAllKeys() {
		return testModelDao.getAllKeys();
	}

	@Override
	public void createTestModel(Model testModel) {
		testModelDao.save(testModel);
	}

}

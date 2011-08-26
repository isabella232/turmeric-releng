package org.ebayopensource.turmeric.utils.cassandra.service;

import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;

public interface CassandraTestService {
	
		  public Model getTestModel(String key);
		  
		  public void deleteTestModel(Model testModel);
		  
		  public Set<String> getAllKeys();
		  
		  public void createTestModel(Model testModel);

}

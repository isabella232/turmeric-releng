package org.ebayopensource.turmeric.utils.cassandra.dao;

import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;

public interface ModelDao {
	public void save(Model testModel);

	public Set<String> getAllKeys();
	
	public Model find(String key);

	public void delete(Model testModel);
}
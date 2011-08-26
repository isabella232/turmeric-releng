package org.ebayopensource.turmeric.utils.cassandra.dao;

import java.util.Set;

import me.prettyprint.hector.api.Keyspace;

import org.ebayopensource.turmeric.utils.cassandra.dao.AbstractColumnFamilyDao;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;

public class ModelDaoImpl extends
		AbstractColumnFamilyDao<String, Model> implements ModelDao {
	
	
	public ModelDaoImpl(final String clusterName,  final String host, final Keyspace keySpace) {
		super(clusterName, host, keySpace.getKeyspaceName(), String.class, Model.class, "TestModels");
	}

	public void save(Model testModel) {
		super.save(testModel.getKey(), testModel);
	}

	public void delete(Model testModel) {
		delete(testModel.getKey());
	}

	@Override
	public Set<String> getAllKeys() {
		return super.getKeys();
	}

}
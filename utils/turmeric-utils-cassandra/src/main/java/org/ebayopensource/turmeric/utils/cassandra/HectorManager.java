/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra;

import org.ebayopensource.turmeric.utils.cassandra.service.CassandraManager;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

/*
 * @author jamuguerza
 */
public class HectorManager {

	public static Cluster getOrCreateCluster(final String clusterName,
			final String host) {
		return HFactory.getOrCreateCluster(clusterName, host);
	}

	public static Keyspace getKeyspace(final String clusterName,
			final String host, final String kspace) {

		Keyspace ks = HFactory.createKeyspace(kspace,
				getOrCreateCluster(clusterName, host));
		
//		if (ks == null){
//			CassandraManager.createKeyspace(kspace);
//			ks = HFactory.createKeyspace(kspace,
//					getOrCreateCluster(clusterName, host));
//		}
		
		return ks;
	}

}

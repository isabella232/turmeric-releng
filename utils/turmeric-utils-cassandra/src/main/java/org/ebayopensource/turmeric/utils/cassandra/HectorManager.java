/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ebayopensource.turmeric.utils.ContextUtils;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
/*
 * @author jamuguerza
 */
public class HectorManager {

	private static Keyspace keyspace;

	public static Cluster getOrCreateCluster(String clusterName , final String host) {
		return HFactory.getOrCreateCluster(clusterName, host);
	}
	
	
	public static Keyspace getKeyspace(final String clusterName, final String host, final String kspace) {
		// Keyspace name retrieve form properties
		if (keyspace == null) {
			keyspace = HFactory.createKeyspace(kspace, getOrCreateCluster(clusterName, host));
		}
		return keyspace;
	}


}

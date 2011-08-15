/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;

public class HectorManager {

	private static Keyspace keyspace;
	private final static StringSerializer serializer = StringSerializer.get();

	private static Mutator mutator;

	public static Cluster getOrCreateCluster() {
		// retreive form properties
		// clusterName, hostIp
		return HFactory.getOrCreateCluster("TurmericCluster",
				"192.168.2.101:9160");
	}

	public static Keyspace getKeyspace(final String space) {
		// Keyspace name retrieve form properties
		if (keyspace == null) {
			keyspace = HFactory.createKeyspace(space, getOrCreateCluster());
			mutator = HFactory.createMutator(keyspace, serializer);
		}
		return keyspace;
	}

	public static void insert(final String columnFamily, final String key,
			final String name, final String value) {
		try {

			mutator.insert(key, columnFamily,
					HFactory.createStringColumn(name, value));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String get(final String columnFamily, final String column,
			final String key) {

		SliceQuery<String, String, String> createSliceQuery = HFactory
				.createSliceQuery(keyspace, serializer, serializer, serializer);
		createSliceQuery.setColumnFamily(columnFamily);
		createSliceQuery.setColumnNames(column);
		createSliceQuery.setKey(key);
		QueryResult<ColumnSlice<String, String>> execute2 = createSliceQuery
				.execute();
		execute2.get().getColumnByName(column).getValue();

		return execute2.toString();
	}

}

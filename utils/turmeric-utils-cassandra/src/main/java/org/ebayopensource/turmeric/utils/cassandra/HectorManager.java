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

//	private static final String cassandraPropFilePath = "META-INF/config/cassandra/cassandra.properties";
//
//	private static final String c_hostIp = "cassandra-host-ip";
//	private static final String c_rpcPort = "cassandra-rpc-port";
//	private final static StringSerializer serializer = StringSerializer.get();
//	
	private static Keyspace keyspace;
	private static Mutator mutator;

	public static Cluster getOrCreateCluster(final String host) {
		return HFactory.getOrCreateCluster("TurmericCluster", host);
	}

	public static Keyspace getKeyspace(final String host, final String space) {
		// Keyspace name retrieve form properties
		if (keyspace == null) {
			keyspace = HFactory.createKeyspace(space, getOrCreateCluster(host));
		}
		return keyspace;
	}

//	public static void insert(final String keyspace, final String columnFamily, final String key,
//			final String name, final String value) {
//		try {
//			
//			mutator = HFactory.createMutator(getKeyspace(keyspace), serializer);
//			mutator.insert(key, columnFamily,
//					HFactory.createStringColumn(name, value));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static String get(final String keyspace, final String columnFamily, final String column,
//			final String key) {
//
//		SliceQuery<String, String, String> createSliceQuery = HFactory
//				.createSliceQuery(getKeyspace(keyspace), serializer, serializer, serializer);
//		createSliceQuery.setColumnFamily(columnFamily);
//		createSliceQuery.setColumnNames(column);
//		createSliceQuery.setKey(key);
//		QueryResult<ColumnSlice<String, String>> execute2 = createSliceQuery
//				.execute();
//		return execute2.get().getColumnByName(column).getValue();
//
//	}
//
//	private static String getHost() {
//		ClassLoader classLoader = ContextUtils.getClassLoader();
//		InputStream inStream = classLoader
//				.getResourceAsStream(cassandraPropFilePath);
//		String host = null;
//		if (inStream != null) {
//			Properties properties = new Properties();
//			try {
//				properties.load(inStream);
//				host = (String) properties.get(c_hostIp) + ":"
//						+ (String) properties.get(c_rpcPort);
//
//			} catch (IOException e) {
//				// ignore
//			} finally {
//				try {
//					inStream.close();
//				} catch (IOException e) {
//					// ignore
//				}
//			}
//		}
//		return host;
//	}

}

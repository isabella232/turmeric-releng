/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.hector;

import java.util.ArrayList;

import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HInvalidRequestException;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * The Class HectorManager.
 * 
 * @author jamuguerza
 */
public class HectorManager {
	
	public static ComparatorType LONGTYPE = ComparatorType.LONGTYPE;
	

	/**
	 * Gets the or create cluster.
	 * 
	 * @param clusterName
	 *            the cluster name
	 * @param host
	 *            the host
	 * @return the or create cluster
	 */
	private static Cluster getOrCreateCluster(final String clusterName,
			final String host) {
		return HFactory.getOrCreateCluster(clusterName, host);
	}

	/**
	 * Gets the keyspace.
	 * 
	 * @param clusterName
	 *            the cluster name
	 * @param host
	 *            the host
	 * @param kspace
	 *            the kspace
	 * @param columnFamilyName
	 *            the column family name
	 * @return the keyspace
	 */
	public Keyspace getKeyspace(final String clusterName, final String host,
			final String kspace, final String columnFamilyName, boolean isSuperColumn, 
			Class<?> superKeyTypeClass, Class<?> keyTypeClass){
		
	ComparatorType superKeyValidator = HectorHelper.getComparator(superKeyTypeClass);
	ComparatorType keyValidator = HectorHelper.getComparator(keyTypeClass);
	
	ComparatorType superComparator = HectorHelper.getComparator(keyTypeClass);
	ComparatorType comparator = HectorHelper.getComparator(String.class);
		
		Keyspace ks = null;

		try {

			ks = createKeyspace(clusterName, host, kspace, columnFamilyName, isSuperColumn, superKeyValidator, keyValidator, superComparator, comparator);

		} catch (HInvalidRequestException e) {
			// ignore it, it means keyspace already exists, but CF could not
			if ("Keyspace already exists.".equalsIgnoreCase(e.getWhy())) {
				try {

					ks = createKeyspaceRetry(clusterName, host, kspace, columnFamilyName, isSuperColumn,superKeyValidator, keyValidator, superComparator, comparator);

				} catch (HInvalidRequestException e1) {
					// ignore it, it means keyspace & CF already exist, get the
					// ks to hector client
					if ((columnFamilyName + " already exists in keyspace " + kspace)
							.equalsIgnoreCase(e1.getWhy())) {

						ks = HFactory.createKeyspace(kspace,
								getOrCreateCluster(clusterName, host));
					}
				}
			}
		}

		return ks;
	}

	/**
	 * Creates the keyspace.
	 * 
	 * @param clusterName
	 *            the cluster name
	 * @param host
	 *            the host
	 * @param kspace
	 *            the kspace
	 * @param columnFamilyName
	 *            the column family name
	 * @return the keyspace
	 */
	private Keyspace createKeyspace(final String clusterName,
			final String host, final String kspace,
			final String columnFamilyName, boolean isSuperColumn, 
			final ComparatorType superKeyValidator, final ComparatorType keyValidator,
			final ComparatorType superComparator, final ComparatorType comparator) {
		Cluster cluster = getOrCreateCluster(clusterName, host);

		KeyspaceDefinition ksDefinition = new ThriftKsDef(kspace);
		Keyspace keyspace = HFactory.createKeyspace(kspace, cluster);
		cluster.addKeyspace(ksDefinition);
	
		createCF(kspace, columnFamilyName, cluster, isSuperColumn, superKeyValidator, keyValidator, superComparator, comparator);
		return keyspace;
	}

	private void createCF(final String kspace, final String columnFamilyName,
			final Cluster cluster, boolean isSuperColumn, final ComparatorType superKeyValidator,
			final ComparatorType keyValidator, 	final ComparatorType superComparator,
			final ComparatorType comparator) {
		
		if(isSuperColumn){
			ThriftCfDef cfDefinition = (ThriftCfDef)HFactory.createColumnFamilyDefinition(kspace, columnFamilyName, superComparator, new ArrayList<ColumnDefinition>() );
			cfDefinition.setColumnType( ColumnType.SUPER);
			cfDefinition.setKeyValidationClass(superKeyValidator.getClassName());
			cfDefinition.setSubComparatorType(comparator);
			cluster.addColumnFamily( cfDefinition );
		}else{
			ColumnFamilyDefinition cfDefinition = new ThriftCfDef(kspace,
					columnFamilyName);
			cfDefinition.setKeyValidationClass(keyValidator.getClassName());
			if("MetricValuesByIpAndDate".equals(columnFamilyName) ||
					"MetricTimeSeries".equals(columnFamilyName) ||
					"ServiceCallsByTime".equals(columnFamilyName) ){
				
				ComparatorType  comparator1 = HectorHelper.getComparator(Long.class);
				cfDefinition.setComparatorType(comparator1);
			}else{
				cfDefinition.setComparatorType(comparator);	
			}
			
			cluster.addColumnFamily(cfDefinition);
		}
	}

	/**
	 * Creates the cf.
	 * 
	 * @param clusterName
	 *            the cluster name
	 * @param host
	 *            the host
	 * @param kspace
	 *            the kspace
	 * @param columnFamilyName
	 *            the column family name
	 * @return the keyspace
	 */
	private Keyspace createKeyspaceRetry(final String clusterName, final String host,
			final String kspace, final String columnFamilyName, boolean isSuperColumn, 
			final ComparatorType superKeyValidator, final ComparatorType keyValidator,
			final ComparatorType superComparator,  final ComparatorType comparator) {

		Cluster cluster = getOrCreateCluster(clusterName, host);

		createCF(kspace, columnFamilyName, cluster, isSuperColumn, superKeyValidator, keyValidator, superComparator, comparator);
		
		Keyspace keyspace = HFactory.createKeyspace(kspace,
				getOrCreateCluster(clusterName, host));

		return keyspace;
	}

}

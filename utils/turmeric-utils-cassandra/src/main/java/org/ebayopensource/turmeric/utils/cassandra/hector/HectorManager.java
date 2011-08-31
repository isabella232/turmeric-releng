/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.hector;

import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HInvalidRequestException;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * The Class HectorManager.
 * 
 * @author jamuguerza
 */
public class HectorManager {

	/**
	 * Gets the or create cluster.
	 * 
	 * @param clusterName
	 *            the cluster name
	 * @param host
	 *            the host
	 * @return the or create cluster
	 */
	public static Cluster getOrCreateCluster(final String clusterName,
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
			final String kspace, final String columnFamilyName) {

		Keyspace ks = null;

		try {

			ks = createKeyspace(clusterName, host, kspace, columnFamilyName);

		} catch (HInvalidRequestException e) {
			// ignore it, it means keyspace already exists, but CF could not
			if ("Keyspace already exists.".equalsIgnoreCase(e.getWhy())) {
				try {

					ks = createCF(clusterName, host, kspace, columnFamilyName);

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
			final String columnFamilyName) {
		Cluster cluster = getOrCreateCluster(clusterName, host);

		KeyspaceDefinition ksDefinition = new ThriftKsDef(kspace);
		Keyspace keyspace = HFactory.createKeyspace(kspace, cluster);
		cluster.addKeyspace(ksDefinition);

		ColumnFamilyDefinition familyDefinition = new ThriftCfDef(kspace,
				columnFamilyName);
		cluster.addColumnFamily(familyDefinition);

		return keyspace;
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
	private Keyspace createCF(final String clusterName, final String host,
			final String kspace, final String columnFamilyName) {

		Cluster cluster = getOrCreateCluster(clusterName, host);

		ColumnFamilyDefinition familyDefinition = new ThriftCfDef(kspace,
				columnFamilyName);
		cluster.addColumnFamily(familyDefinition);

		Keyspace keyspace = HFactory.createKeyspace(kspace,
				getOrCreateCluster(clusterName, host));

		return keyspace;
	}

}

/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.dao;

import java.util.HashSet;
import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.HectorHelper;
import org.ebayopensource.turmeric.utils.cassandra.HectorManager;

import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

/*
 * @author jamuguerza
 */
/**
 * The Class AbstractColumnFamilyDao.
 * 
 * @param <KeyType>
 *            the generic type
 * @param <T>
 *            the generic type
 */
public abstract class AbstractColumnFamilyDao<KeyType, T> {

	/** The persistent class. */
	private final Class<T> persistentClass;

	/** The key type class. */
	private final Class<KeyType> keyTypeClass;

	/** The key space. */
	protected final Keyspace keySpace;

	/** The column family name. */
	protected final String columnFamilyName;

	/** The all column names. */
	private final String[] allColumnNames;

	/**
	 * Instantiates a new abstract column family dao.
	 * 
	 * @param clusterName
	 *            the clusterName
	 * @param host
	 *            the host
	 * @param s_keyspace
	 *            the s_keyspace
	 * @param keyTypeClass
	 *            the key type class
	 * @param persistentClass
	 *            the persistent class
	 * @param columnFamilyName
	 *            the column family name
	 */
	public AbstractColumnFamilyDao(final String clusterName, final String host,
			final String s_keyspace, final Class<KeyType> keyTypeClass,
			final Class<T> persistentClass, final String columnFamilyName) {
		this.keySpace = HectorManager
				.getKeyspace(clusterName, host, s_keyspace);
		this.keyTypeClass = keyTypeClass;
		this.persistentClass = persistentClass;
		this.columnFamilyName = columnFamilyName;
		this.allColumnNames = HectorHelper.getAllColumnNames(persistentClass);
	}

	/**
	 * Save.
	 * 
	 * @param key
	 *            the key
	 * @param model
	 *            the model
	 */
	public void save(KeyType key, T model) {

		Mutator<Object> mutator = HFactory.createMutator(keySpace,
				SerializerTypeInferer.getSerializer(keyTypeClass));
		for (HColumn<?, ?> column : HectorHelper.getColumns(model)) {
			mutator.addInsertion(key, columnFamilyName, column);
		}

		mutator.execute();
	}

	/**
	 * Find.
	 * 
	 * @param key
	 *            the key
	 * @return the t
	 */
	public T find(KeyType key) {
		SliceQuery<Object, String, Object> query = HFactory.createSliceQuery(
				keySpace, SerializerTypeInferer.getSerializer(keyTypeClass),
				StringSerializer.get(), ObjectSerializer.get());

		QueryResult<ColumnSlice<String, Object>> result = query
				.setColumnFamily(columnFamilyName).setKey(key)
				.setColumnNames(allColumnNames).execute();

		if (result.get().getColumns().size() == 0) {
			return null;
		}

		try {
			T t = persistentClass.newInstance();
			HectorHelper.populateEntity(t, result);
			return t;
		} catch (Exception e) {
			throw new RuntimeException("Error creating persistent class", e);
		}
	}

	/**
	 * Delete.
	 * 
	 * @param key
	 *            the key
	 */
	public void delete(KeyType key) {
		Mutator<Object> mutator = HFactory.createMutator(keySpace,
				SerializerTypeInferer.getSerializer(keyTypeClass));
		mutator.delete(key, columnFamilyName, null,
				SerializerTypeInferer.getSerializer(keyTypeClass));
	}

	/**
	 * Gets the keys.
	 * 
	 * @return the keys
	 */
	public Set<String> getKeys() {
		int rows = 0;
		Set<String> rowKeys = new HashSet<String>();
		Row<Object, String, Object> lastRow = null;

		do {
			RangeSlicesQuery<Object, String, Object> rangeSlicesQuery = HFactory
					.createRangeSlicesQuery(keySpace,
							SerializerTypeInferer.getSerializer(keyTypeClass),
							StringSerializer.get(), ObjectSerializer.get());
			rangeSlicesQuery.setColumnFamily(columnFamilyName);
			if (lastRow != null) {
				rangeSlicesQuery.setKeys(lastRow.getKey(), "");
			} else {
				rangeSlicesQuery.setKeys("", "");
			}
			rangeSlicesQuery.setReturnKeysOnly();
			rangeSlicesQuery.setRange("", "", false, 3);
			rangeSlicesQuery.setRowCount(10);
			QueryResult<OrderedRows<Object, String, Object>> result = rangeSlicesQuery
					.execute();
			OrderedRows<Object, String, Object> orderedRows = result.get();
			rows = orderedRows.getCount();

			for (Row<Object, String, Object> row : orderedRows) {
				rowKeys.add((String) row.getKey());
			}

			lastRow = orderedRows.peekLast();

		} while (rows > 0);

		return rowKeys;
	}

	/**
	 * Contains.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public boolean containsKey(KeyType key) {
		RangeSlicesQuery<Object, String, Object> rangeSlicesQuery = HFactory
				.createRangeSlicesQuery(keySpace,
						SerializerTypeInferer.getSerializer(keyTypeClass),
						StringSerializer.get(), ObjectSerializer.get());
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setKeys(key, "");
		rangeSlicesQuery.setReturnKeysOnly();
		rangeSlicesQuery.setRange("", "", false, 3);
		rangeSlicesQuery.setRowCount(1);
		QueryResult<OrderedRows<Object, String, Object>> result = rangeSlicesQuery
				.execute();
		OrderedRows<Object, String, Object> orderedRows = result.get();

		return (orderedRows.getCount() >= 0);
	}

}

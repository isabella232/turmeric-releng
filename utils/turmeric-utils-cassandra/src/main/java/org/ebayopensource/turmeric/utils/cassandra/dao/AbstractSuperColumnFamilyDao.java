/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.OrderedSuperRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperRows;
import me.prettyprint.hector.api.beans.SuperSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSuperSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.RangeSuperSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import me.prettyprint.hector.api.query.SuperSliceQuery;

import org.ebayopensource.turmeric.utils.cassandra.hector.HectorHelper;
import org.ebayopensource.turmeric.utils.cassandra.hector.HectorManager;

/**
 * The Class AbstractColumnFamilyDao.
 * 
 * @author jamuguerza
 * @param <KeyType>
 *            the generic type
 * @param <T>
 *            the generic type
 */
public abstract class AbstractSuperColumnFamilyDao<SKeyType, ST, KeyType, T> {

	/** The super key type class. */
	private final Class<SKeyType> superKeyTypeClass;

	/** The key type class. */
	private final Class<KeyType> keyTypeClass;

	/** The key space. */
	protected final Keyspace keySpace;

	/** The column family name. */
	protected final String columnFamilyName;
	
	/** The persistent class. */
	private final Class<T> persistentClass;
	
	/** The super persistent class. */
	private final Class<ST> superPersistentClass;
	
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
	public AbstractSuperColumnFamilyDao(final String clusterName, final String host,
			final String s_keyspace, final Class<SKeyType> superKeyTypeClass,
			final Class<ST> superPersistentClass, final Class<KeyType> keyTypeClass,
			final Class<T> persistentClass, final String columnFamilyName) {
		this.keySpace = new HectorManager().getKeyspace(clusterName, host,
				s_keyspace, columnFamilyName, true);
		
		this.superKeyTypeClass = superKeyTypeClass;
		this.keyTypeClass = keyTypeClass;
		this.columnFamilyName = columnFamilyName;
		this.superPersistentClass = superPersistentClass;
		this.persistentClass = persistentClass;

		this.allColumnNames = HectorHelper.getAllColumnNames(superPersistentClass); 
	}

	
	
	/**
	 * Save.
	 *
	 * @param superkey the superkey
	 * @param key the key
	 * @param superModel the super model
	 * @param model the model
	 * 
	 * 
	 * At this moment the internal data in a super column must be string for keys and String for values. 
	 * Not generics supported yet
	 */
	public void save(SKeyType superKey, ST superModel, Map<String,T> modelMap) {
		
	    Mutator<Object> mutator = HFactory.createMutator(keySpace,
				SerializerTypeInferer.getSerializer(superKeyTypeClass));
		
	    for (String  key : modelMap.keySet()) {
	    	T t = modelMap.get(key);
	    	List<HColumn<String, Object>> columns = HectorHelper.getObjectColumns(t);
			 	HSuperColumn<Object, String, Object> superColumn = HFactory.createSuperColumn(superKey, columns,    SerializerTypeInferer.getSerializer(superKeyTypeClass), StringSerializer.get(),
			 			ObjectSerializer.get());
			 	
			mutator.addInsertion(superKey, columnFamilyName, superColumn);

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
	public ST find(SKeyType superKey) {
		SuperSliceQuery<Object, Object, String, byte[]> superQuery = HFactory.createSuperSliceQuery(keySpace,  SerializerTypeInferer.getSerializer(superKeyTypeClass),SerializerTypeInferer.getSerializer(keyTypeClass), StringSerializer.get() ,BytesArraySerializer.get());

		QueryResult<SuperSlice<Object, String, byte[]>> result = superQuery
				.setColumnFamily(columnFamilyName).setKey(superKey)
				.setColumnNames(allColumnNames).execute();

		if (result.get().getSuperColumns().size() == 0) {
			return null;
		}

		try {
			ST st = superPersistentClass.newInstance();
			T t = persistentClass.newInstance();
			HectorHelper.populateSuperEntity(st, t, result);
			return st;
		} catch (Exception e) {
			throw new RuntimeException("Error creating persistent class", e);
		}
	}


	/**
	 * Find super items.
	 *
	 * @param superKeys the super keys
	 * @param superColNames the super col names
	 * @param keys the keys
	 * @param rangeFrom the range from
	 * @param rangeTo the range to
	 * @return the map
	 */
	public Map<SKeyType, ST> findSuperItems(final List<SKeyType> superKeys, 	final List<String> superColNames, 
			 final List<KeyType> keys, final String rangeFrom, final String rangeTo){
		
		Map<SKeyType,ST> items = new HashMap<SKeyType, ST>();

		MultigetSuperSliceQuery<Object, Object, String, byte[]> multigetSuperSliceQuery = HFactory
		.createMultigetSuperSliceQuery(keySpace,
				SerializerTypeInferer.getSerializer(superKeyTypeClass),
				SerializerTypeInferer.getSerializer(keyTypeClass), StringSerializer.get() ,  BytesArraySerializer.get());
		
		multigetSuperSliceQuery.setColumnFamily(columnFamilyName);
		multigetSuperSliceQuery.setKeys(superKeys.toArray());
		multigetSuperSliceQuery.setColumnNames(superColNames);
		multigetSuperSliceQuery.setRange(rangeFrom, rangeTo, false ,Integer.MAX_VALUE);

		QueryResult<SuperRows<Object, Object, String, byte[]>> result = multigetSuperSliceQuery .execute();
		
		for (SuperRow<Object, Object, String, byte[]> row : result.get()) {
			if(! row.getSuperSlice().getSuperColumns().isEmpty()){
				//ip all
				List<HSuperColumn<Object, String, byte[]>> superColumns = row.getSuperSlice().getSuperColumns();
				for (HSuperColumn<Object, String, byte[]> hSuperColumn : superColumns) {
					if(!hSuperColumn.getColumns().isEmpty()){
						items.put((SKeyType) hSuperColumn.getName(), (ST) hSuperColumn.getColumns());
					}	
					
				}
			}
			
		}
		
		return items;
	}
	
	/**
	 * Delete.
	 * 
	 * @param key
	 *            the key
	 * @see http://wiki.apache.org/cassandra/DistributedDeletes
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
		int pagination = 50;
		Set<String> rowKeys = new HashSet<String>();

		SuperRow<Object, String, String, byte[]> lastRow = null;

		do {
			RangeSuperSlicesQuery<Object, String, String, byte[]> rangeSuperSliceQuery = HFactory
					.createRangeSuperSlicesQuery(keySpace, SerializerTypeInferer.getSerializer(superKeyTypeClass),
							StringSerializer.get(), StringSerializer.get(), BytesArraySerializer.get());
			
			rangeSuperSliceQuery.setColumnFamily(columnFamilyName);
			if (lastRow != null) {
				rangeSuperSliceQuery.setKeys(lastRow.getKey(), "");
			} else {
				rangeSuperSliceQuery.setKeys("", "");
			}
			rangeSuperSliceQuery.setRange("","", false, 2);
			rangeSuperSliceQuery.setRowCount(pagination);

			QueryResult<OrderedSuperRows<Object, String, String, byte[]>> result = rangeSuperSliceQuery
					.execute();
			OrderedSuperRows<Object, String, String, byte[]> orderedSuperRows = result.get();
			rows = orderedSuperRows.getCount();

			for (SuperRow<Object, String, String, byte[]> row : orderedSuperRows) {
				if (!row.getSuperSlice().getSuperColumns().isEmpty()) {
					rowKeys.add((String) row.getKey());
					lastRow = orderedSuperRows.getList().get(rows -1);
				}
			}

		} while (rows == pagination);

		return rowKeys;

	}

	
}
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
import java.util.List;
import java.util.Set;

import me.prettyprint.cassandra.serializers.BytesArraySerializer;
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

import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
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

    /** The column count. */
    private final int columnCount;

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
        this.keySpace = new HectorManager().getKeyspace(clusterName, host,
                s_keyspace, columnFamilyName);
        this.keyTypeClass = keyTypeClass;
        this.persistentClass = persistentClass;
        this.columnFamilyName = columnFamilyName;
        this.allColumnNames = HectorHelper.getAllColumnNames(persistentClass);
        this.columnCount = HectorHelper.getColumnCount(persistentClass);
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
        SliceQuery<Object, String, byte[]> query = HFactory.createSliceQuery(
                keySpace, SerializerTypeInferer.getSerializer(keyTypeClass),
                StringSerializer.get(), BytesArraySerializer.get());

        QueryResult<ColumnSlice<String, byte[]>> result = query
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

        Row<Object, String, byte[]> lastRow = null;

        do {
            RangeSlicesQuery<Object, String, byte[]> rangeSlicesQuery = HFactory
                    .createRangeSlicesQuery(keySpace,
                            SerializerTypeInferer.getSerializer(keyTypeClass),
                            StringSerializer.get(), BytesArraySerializer.get());
            rangeSlicesQuery.setColumnFamily(columnFamilyName);
            if (lastRow != null) {
                rangeSlicesQuery.setKeys(lastRow.getKey(), "");
            } else {
                rangeSlicesQuery.setKeys("", "");
            }
            rangeSlicesQuery.setReturnKeysOnly();
            rangeSlicesQuery.setRange("", "", false,
                    keyTypeClass.getDeclaredFields().length);
            rangeSlicesQuery.setRowCount(pagination);
            QueryResult<OrderedRows<Object, String, byte[]>> result = rangeSlicesQuery
                    .execute();
            OrderedRows<Object, String, byte[]> orderedRows = result.get();
            rows = orderedRows.getCount();

            for (Row<Object, String, byte[]> row : orderedRows) {
                if (!row.getColumnSlice().getColumns().isEmpty()) {
                    rowKeys.add((String) row.getKey());
                }
            }

            lastRow = orderedRows.peekLast();

        } while (rows == pagination);

        return rowKeys;

    }

    /**
     * Contains.
     * 
     * @param key
     *            the key
     * @return true, if successful
     * @see http://wiki.apache.org/cassandra/DistributedDeletes
     */
    public boolean containsKey(KeyType key) {
        RangeSlicesQuery<Object, String, byte[]> rangeSlicesQuery = HFactory
                .createRangeSlicesQuery(keySpace,
                        SerializerTypeInferer.getSerializer(keyTypeClass),
                        StringSerializer.get(), BytesArraySerializer.get());
        rangeSlicesQuery.setColumnFamily(columnFamilyName);
        rangeSlicesQuery.setKeys(key, key);
        rangeSlicesQuery.setReturnKeysOnly();
        rangeSlicesQuery.setRange("", "", false, 3);
        rangeSlicesQuery.setRowCount(1);
        QueryResult<OrderedRows<Object, String, byte[]>> result = rangeSlicesQuery
                .execute();
        OrderedRows<Object, String, byte[]> orderedRows = result.get();

        return (!orderedRows.getList().isEmpty() && !orderedRows.getByKey(key)
                .getColumnSlice().getColumns().isEmpty());
    }

}
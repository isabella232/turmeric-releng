/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.dao;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.beans.OrderedSuperRows;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperRows;
import me.prettyprint.hector.api.beans.SuperSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSuperSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSuperSlicesQuery;
import me.prettyprint.hector.api.query.SuperSliceQuery;

import org.ebayopensource.turmeric.utils.cassandra.hector.HectorHelper;
import org.ebayopensource.turmeric.utils.cassandra.hector.HectorManager;

/**
 * The Class AbstractColumnFamilyDao.
 * 
 * @param <SKeyType>
 *           the generic type
 * @param <ST>
 *           the generic type
 * @param <KeyType>
 *           the generic type
 * @param <T>
 *           the generic type
 * @author jamuguerza
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

   /**
    * Instantiates a new abstract column family dao.
    * 
    * @param clusterName
    *           the clusterName
    * @param host
    *           the host
    * @param s_keyspace
    *           the s_keyspace
    * @param superKeyTypeClass
    *           the super key type class
    * @param superPersistentClass
    *           the super persistent class
    * @param keyTypeClass
    *           the key type class
    * @param persistentClass
    *           the persistent class
    * @param columnFamilyName
    *           the column family name
    */

   public AbstractSuperColumnFamilyDao(final String clusterName, final String host, final String s_keyspace,
            final Class<SKeyType> superKeyTypeClass, final Class<ST> superPersistentClass,
            final Class<KeyType> keyTypeClass, final Class<T> persistentClass, final String columnFamilyName) {
      this.keySpace = new HectorManager().getKeyspace(clusterName, host, s_keyspace, columnFamilyName, true,
               superKeyTypeClass, keyTypeClass);

      this.superKeyTypeClass = superKeyTypeClass;
      this.keyTypeClass = keyTypeClass;
      this.columnFamilyName = columnFamilyName;
      this.superPersistentClass = superPersistentClass;
      this.persistentClass = persistentClass;

   }

   /**
    * Save.
    * 
    * @param superKey
    *           the super key
    * @param modelMap
    *           the model map
    */
   public void save(SKeyType superKey, Map<KeyType, T> modelMap) {

      Mutator<Object> mutator = HFactory
               .createMutator(keySpace, SerializerTypeInferer.getSerializer(superKeyTypeClass));

      for (KeyType key : modelMap.keySet()) {
         T t = modelMap.get(key);
         List<HColumn<String, Object>> columns = HectorHelper.getObjectColumns(t);
         HSuperColumn<Object, String, Object> superColumn = HFactory.createSuperColumn(key, columns,
                  SerializerTypeInferer.getSerializer(keyTypeClass), StringSerializer.get(), ObjectSerializer.get());

         mutator.addInsertion(superKey, columnFamilyName, superColumn);

      }
      mutator.execute();

   }

   public boolean containsKey(final SKeyType superKey) {

      MultigetSuperSliceQuery<Object, Object, String, byte[]> multigetSuperSliceQuery = HFactory
               .createMultigetSuperSliceQuery(keySpace, SerializerTypeInferer.getSerializer(superKeyTypeClass),
                        SerializerTypeInferer.getSerializer(keyTypeClass), StringSerializer.get(),
                        BytesArraySerializer.get());

      multigetSuperSliceQuery.setColumnFamily(columnFamilyName);
      multigetSuperSliceQuery.setKeys(superKey);

      multigetSuperSliceQuery.setRange(null, null, false, Integer.MAX_VALUE);

      QueryResult<SuperRows<Object, Object, String, byte[]>> result = multigetSuperSliceQuery.execute();

      try {
         return (!result.get().getByKey(superKey).getSuperSlice().getSuperColumns().isEmpty());

      } catch (Exception e) {
         return false;
      }

   }

   /**
    * Find.
    * 
    * @param fromSName
    *           the from s name
    * @param toSName
    *           the to s name
    * @return the sT
    */
   public List<ST> findByRange(final KeyType fromSName, final KeyType toSName) {
      List<ST> stList = new ArrayList<ST>();

      List<HSuperColumn<Object, String, byte[]>> superColumns = null;

      RangeSuperSlicesQuery<Object, Object, String, byte[]> superColumnQuery = HFactory.createRangeSuperSlicesQuery(
               keySpace, SerializerTypeInferer.getSerializer(superKeyTypeClass),
               SerializerTypeInferer.getSerializer(keyTypeClass), StringSerializer.get(), BytesArraySerializer.get());
      superColumnQuery.setColumnFamily(columnFamilyName).setKeys("", "");
      superColumnQuery.setRange(fromSName, toSName, false, 50);

      QueryResult<OrderedSuperRows<Object, Object, String, byte[]>> result = superColumnQuery.execute();
      for (SuperRow<Object, Object, String, byte[]> superRow : result.get()) {
         SKeyType superKey = (SKeyType) superRow.getKey();

         try {
            superColumns = superRow.getSuperSlice().getSuperColumns();

            if (superColumns.isEmpty()) {
               continue;
            }
         } catch (Exception e) {
            continue;
         }

         try {
            Constructor<?>[] constructorsST = superPersistentClass.getConstructors();
            ST st = (ST) constructorsST[0].newInstance(superKeyTypeClass, keyTypeClass);

            Constructor<?>[] constructorsT = persistentClass.getConstructors();
            T t = (T) constructorsT[0].newInstance(keyTypeClass);

            HectorHelper.populateSuperEntity(st, t, superKey, keyTypeClass, superColumns);
            stList.add(st);
         } catch (Exception e) {
            throw new RuntimeException("Error creating persistent class", e);
         }
      }
      return stList;
   }

   /**
    * Find.
    * 
    * @param superKey
    *           the super key
    * @param columnNames
    *           Optional the column names
    * @return the t
    */
   public ST find(final SKeyType superKey, final KeyType[] superColumnNames) {

      List<HSuperColumn<Object, String, byte[]>> superColumns = null;

      SuperSliceQuery<Object, Object, String, byte[]> superColumnQuery = HFactory.createSuperSliceQuery(keySpace,
               SerializerTypeInferer.getSerializer(superKeyTypeClass),
               SerializerTypeInferer.getSerializer(keyTypeClass), StringSerializer.get(), BytesArraySerializer.get());
      superColumnQuery.setColumnFamily(columnFamilyName).setKey(superKey);
      if (superColumnNames == null
               || (superColumnNames.length == 0)
               || (superColumnNames.length > 0 && ("".equals(superColumnNames[0]) || "All".equals(superColumnNames[0])))) {
         superColumnQuery.setRange(null, null, false, 50);
      } else {
         superColumnQuery.setColumnNames(superColumnNames);
      }

      QueryResult<SuperSlice<Object, String, byte[]>> result = superColumnQuery.execute();

      try {
         superColumns = result.get().getSuperColumns();

         if (superColumns.isEmpty()) {
            return null;
         }
      } catch (Exception e) {
         return null;
      }

      try {
         Constructor<?>[] constructorsST = superPersistentClass.getConstructors();
         ST st = (ST) constructorsST[0].newInstance(superKeyTypeClass, keyTypeClass);

         Constructor<?>[] constructorsT = persistentClass.getConstructors();
         T t = (T) constructorsT[0].newInstance(keyTypeClass);

         HectorHelper.populateSuperEntity(st, t, superKey, keyTypeClass, superColumns);
         return st;
      } catch (Exception e) {
         throw new RuntimeException("Error creating persistent class", e);
      }
   }

   /**
    * Find super items.
    * 
    * @param superKeys
    *           the super keys
    * @param columnNames
    *           Optional the column names
    * @return the map
    */
   public Map<SKeyType, ST> findItems(final List<SKeyType> superKeys, final KeyType[] superColumnNames) {

      Map<SKeyType, ST> result = new HashMap<SKeyType, ST>();
      for (SKeyType superKey : superKeys) {
         result.put(superKey, find(superKey, superColumnNames));
      }

      return result;
   }

   /**
    * Delete.
    * 
    * @param superKey
    *           the super key
    * @see http://wiki.apache.org/cassandra/DistributedDeletes
    */
   public void delete(SKeyType superKey) {
      Mutator<Object> mutator = HFactory
               .createMutator(keySpace, SerializerTypeInferer.getSerializer(superKeyTypeClass));
      mutator.delete(superKey, columnFamilyName, null, SerializerTypeInferer.getSerializer(superKeyTypeClass));
   }

   /**
    * Gets the keys.
    * 
    * @return the keys
    */
   public Set<KeyType> getKeys() {
      int rows = 0;
      int pagination = 50;
      Set<KeyType> rowKeys = new HashSet<KeyType>();

      SuperRow<Object, Object, String, byte[]> lastRow = null;
      do {
         RangeSuperSlicesQuery<Object, Object, String, byte[]> rangeSuperSliceQuery = HFactory
                  .createRangeSuperSlicesQuery(keySpace, SerializerTypeInferer.getSerializer(superKeyTypeClass),
                           SerializerTypeInferer.getSerializer(keyTypeClass), StringSerializer.get(),
                           BytesArraySerializer.get());

         rangeSuperSliceQuery.setColumnFamily(columnFamilyName);
         if (lastRow != null) {
            rangeSuperSliceQuery.setKeys(lastRow.getKey(), "");
         } else {
            rangeSuperSliceQuery.setKeys(null, null);
         }
         rangeSuperSliceQuery.setRange(null, null, false, 2);
         rangeSuperSliceQuery.setRowCount(pagination);

         QueryResult<OrderedSuperRows<Object, Object, String, byte[]>> result = rangeSuperSliceQuery.execute();
         OrderedSuperRows<Object, Object, String, byte[]> orderedSuperRows = result.get();
         rows = orderedSuperRows.getCount();

         for (SuperRow<Object, Object, String, byte[]> row : orderedSuperRows) {
            if (!row.getSuperSlice().getSuperColumns().isEmpty()) {
               rowKeys.add((KeyType) row.getKey());
               lastRow = orderedSuperRows.getList().get(rows - 1);
            }
         }

      } while (rows == pagination);

      return rowKeys;

   }

}
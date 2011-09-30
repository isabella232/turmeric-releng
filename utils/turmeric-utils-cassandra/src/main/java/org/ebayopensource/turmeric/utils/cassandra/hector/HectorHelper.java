/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.hector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;

/*
 * The Class HectorHelper.
 * @author jamuguerza
 */
public final class HectorHelper {

	/**
	 * Instantiates a new hector helper.
	 */
	private HectorHelper() {
	}

	/**
	 * Gets the time uuid.
	 * 
	 * @return the time uuid
	 */
	public static java.util.UUID getTimeUUID() {
		return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
	}

	/**
	 * As byte array.
	 * 
	 * @param uuid
	 *            the uuid
	 * @return the byte[]
	 */
	public static byte[] asByteArray(java.util.UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++) {
			buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;
	}

	/**
	 * Gets the columns.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param entity
	 *            the entity
	 * @return the columns
	 */
	public static <T> List<HColumn<String, ?>> getColumns(T entity) {
		try {
			List<HColumn<String, ?>> columns = new ArrayList<HColumn<String, ?>>();
			Field[] fields = entity.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(entity);

				if (value == null) {
					// Field has no value so nothing to store
					continue;
				}

				String name = field.getName();

				HColumn<String, ?> column = HFactory.createColumn(name, value,
						StringSerializer.get(),
						SerializerTypeInferer.getSerializer(value));

				columns.add(column);
			}
			return columns;
		} catch (Exception e) {
			throw new RuntimeException("Reflection exception", e);
		}
	}

	public static <T> List<HColumn<String, Object>> getObjectColumns(T entity) {
		try {
			List<HColumn<String, Object>> columns = new ArrayList<HColumn<String, Object>>();
			Field[] fields = entity.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(entity);

				if (value == null) {
					// Field has no value so nothing to store
					continue;
				}

				String name = field.getName();

				HColumn<String, Object> column = HFactory.createColumn(name,
						value, StringSerializer.get(), ObjectSerializer.get());

				columns.add(column);
			}
			return columns;
		} catch (Exception e) {
			throw new RuntimeException("Reflection exception", e);
		}
	}


	/**
	 * Gets the string cols.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param entity
	 *            the entity
	 * @return the string cols
	 */
	public static <T> List<HColumn<String, String>> getStringCols(T entity) {
		try {
			List<HColumn<String, ?>> cols = getColumns(entity);
			List<HColumn<String, String>> retCols = new ArrayList<HColumn<String, String>>();

			for (HColumn<String, ?> col : cols) {
				retCols.add(HFactory.createStringColumn(col.getName(), col
						.getValue().toString()));
			}

			return retCols;
		} catch (Exception e) {
			throw new RuntimeException("Reflection away", e);
		}
	}

	/**
	 * Populate entity.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param t
	 *            the t
	 * @param result
	 *            the result
	 */
	public static <T, KeyType> void populateEntity(T t, KeyType key,
			QueryResult<ColumnSlice<String, byte[]>> result) {
		try {
			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();

				if (name.equals("key")) {
					field.set(t, key);
				} else {
					HColumn<String, byte[]> col = result.get().getColumnByName(
							name);
					if (col == null || col.getValue() == null
							|| col.getValueBytes().capacity() == 0) {
						// No data for this col
						continue;
					}

					Object val = SerializerTypeInferer.getSerializer(
							field.getType()).fromBytes(col.getValue());
					field.set(t, val);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Reflection Error ", e);
		}
	}

	public static <ST, T, SKeyType, KeyType> void populateSuperEntity(ST st,
			T t, SKeyType superKey, Class<KeyType> keyTypeClass,
			List<HSuperColumn<Object, String, byte[]>> result) {
		try {
			// load key for ST
			// Assumes a Map for columns and 1 other field for key
			Field[] superFields = st.getClass().getDeclaredFields();
			for (Field superField : superFields) {
				superField.setAccessible(true);
				// the key
				if (!superField.getType().equals(Map.class)) {
					superField.set(st, superKey);
				} else {
					Map<KeyType, T> superColumnMap = new HashMap<KeyType, T>();
					

					Iterator<HSuperColumn<Object, String, byte[]>> superColumnsIterator = result
							.iterator();
					while (superColumnsIterator.hasNext()) {
						HSuperColumn<Object, String, byte[]> superColumn = superColumnsIterator
								.next();
						KeyType superColumnKey = (KeyType) superColumn
								.getName();

						Field[] fields = t.getClass().getDeclaredFields();
						for (Field field : fields) {
							field.setAccessible(true);

							if (!field.getType().equals(Map.class)) {
								field.set(t, superColumnKey);
							} else {
								Map<String, Object> columnsMap = new HashMap<String, Object>();
								List<HColumn<String, byte[]>> columns = superColumn
										.getColumns();
								for (HColumn<String, byte[]> col : columns) {
									if (col == null){
										continue;
									}

									String name =  (String)StringSerializer.get()
									.fromBytes(col.getName().getBytes());

									Object val = SerializerTypeInferer
											.getSerializer(field.getType())
											.fromBytes(col.getValue());
									
									columnsMap.put(name, val);

								}
								field.set(t, columnsMap);
							}
						}

						superColumnMap.put(superColumnKey, t);
					}
					superField.set(st, superColumnMap);

				}
			}

		} catch (IllegalAccessException e) {
			throw new RuntimeException("Reflection Error ", e);
		}
	}

	/**
	 * Gets the field for property name.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param entity
	 *            the entity
	 * @param name
	 *            the name
	 * @return the field for property name
	 */
	public static <T> Field getFieldForPropertyName(T entity, String name) {
		try {
			return entity.getClass().getDeclaredField(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Populate entity from cols.
	 * 
	 * @param entity
	 *            the entity
	 * @param cols
	 *            the cols
	 */
	public static void populateEntityFromCols(Object entity,
			List<HColumn<String, String>> cols) {

		for (HColumn<String, ?> col : cols) {
			Field f = getFieldForPropertyName(entity, col.getName());
			try {
				f.setAccessible(true);
				f.set(entity, col.getValue());
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Gets the all column names.
	 * 
	 * @param entityClass
	 *            the entity class
	 * @return the all column names
	 */
	public static String[] getAllColumnNames(Class<?> entityClass) {
		List<String> columnNames = new ArrayList<String>();
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();
			columnNames.add(name);
		}

		return columnNames.toArray(new String[] {});
	}

	/**
	 * Gets the column count.
	 * 
	 * @param entityClass
	 *            the entity class
	 * @return the column count
	 */
	public static int getColumnCount(Class<?> entityClass) {
		String[] columnNames = getAllColumnNames(entityClass);
		return columnNames.length;
	}

	public static ComparatorType getComparator(Class<?> keyTypeClass) {
		if (keyTypeClass != null && String.class.isAssignableFrom(keyTypeClass)) {
			return ComparatorType.UTF8TYPE;
		} else if (keyTypeClass != null
				&& Integer.class.isAssignableFrom(keyTypeClass)) {
			return ComparatorType.INTEGERTYPE;
		} else if (keyTypeClass != null
				&& Long.class.isAssignableFrom(keyTypeClass)) {
			return ComparatorType.LONGTYPE;
		} else if (keyTypeClass != null
				&& Date.class.isAssignableFrom(keyTypeClass)) {
			return ComparatorType.TIMEUUIDTYPE;
		}  else {
			return ComparatorType.BYTESTYPE; // by default
		}

	}
}

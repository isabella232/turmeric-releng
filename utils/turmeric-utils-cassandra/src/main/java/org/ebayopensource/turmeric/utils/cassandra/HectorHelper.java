/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra;

import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*
 * @author jamuguerza
 */
public final class HectorHelper {

	private HectorHelper() {
	}

	public static java.util.UUID getTimeUUID() {
		return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
	}

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

	public static <T> void populateEntity(T t,
			QueryResult<ColumnSlice<String, byte[]>> result) {
		try {
			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				HColumn<String, byte[]> col = result.get()
						.getColumnByName(name);
				if (col == null || col.getValue() == null
						|| col.getValueBytes().capacity() == 0) {
					// No data for this col
					continue;
				}

				Object val = SerializerTypeInferer.getSerializer(
						field.getType()).fromBytes(col.getValue());
				field.set(t, val);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Reflection Error ", e);
		}
	}

	public static <T> Field getFieldForPropertyName(T entity, String name) {
		try {
			return entity.getClass().getDeclaredField(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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

	public static int getColumnCount(Class<?> entityClass) {
		String[] columnNames = getAllColumnNames(entityClass);
		return columnNames.length;
	}
}

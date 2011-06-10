/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * General-purpose utilities for manipulating objects.
 * 
 * @author mpoplacenel
 */
public class ObjectUtils {
	
	public static boolean bothNullOrEqual(Object o1, Object o2) {
		return o1 == null ? (o2 == null) : o1.equals(o2);
	}
	
	public static int hashCodeOrZero(Object o) {
		return o == null ? 0 : o.hashCode();
	}

	private ObjectUtils() {
		// no instance
	}

	/**
	 * Packages the given key into a single-element array typed to the key's class.
	 * Note: the array may be more specialized than T, as t may be of a subclass of T.
	 * @param <T> the generic type.
	 * @param t the object to package.
	 * @return an array of type <code>t.getClass()</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] createSingleArray(T t) {
		T[] result = (T[]) Array.newInstance(t.getClass(), 1);
		result[0] = t;
		
		return result;
	}

	/**
	 * Packages the given key collection into an array typed to the collections's first element class.
	 * Note: the array may be more specialized than T, as tColl.first() may be of a subclass of T.
	 * @param <T> the generic type.
	 * @param tColl the collection to package.
	 * @return an array of type <code>tColl.first().getClass()</code>, containing the elements of <code>tColl</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] createArray(Collection<T> tColl) {
		if (tColl.isEmpty()) {
			throw new IllegalArgumentException("Empty collection is not supported");
		}
		T t = tColl.iterator().next();
		T[] tArr = (T[]) Array.newInstance(t.getClass(), 1);
		
		return (T[]) tColl.toArray(tArr);
	}

}
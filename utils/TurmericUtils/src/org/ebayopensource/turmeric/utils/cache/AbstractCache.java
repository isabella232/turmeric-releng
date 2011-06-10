/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cache;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.utils.MemoryUsage;
import org.ebayopensource.turmeric.utils.ObjectUtils;

/**
 * Security cache - a somewhat-generic cache implementation that allows pluggable cache building. 
 * 
 * @author mpoplacenel
 */
public abstract class AbstractCache<K, V> {
	
	/**
	 * Class logger. 
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractCache.class.getName());
	
	protected final String name;
	
	protected final CacheBuilder<K, V> cacheBuilder;
	
	private final Map<K, V> cache = new ConcurrentHashMap<K, V>();
	
	private final ConcurrentMap<K, AtomicLong> hitStats = new ConcurrentHashMap<K, AtomicLong>();
	
	public AbstractCache(String name, CacheBuilder<K, V> cacheBuilder) {
		this.name = name;
		this.cacheBuilder = cacheBuilder;
	}
	
	public void init(K... keys) throws CacheBuildingException {
		clearAll();
		
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Requesting cache building for keys: " 
					+ Arrays.asList(keys));
		}
		long preMemUsg = 0L;
		if (MemoryUsage.isEnabled()) {
			preMemUsg = MemoryUsage.getMemoryUsage();
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "%%%%%%%%%%%%%%%% Memory usage pre-init keys: " + Arrays.asList(keys)
						+ " is: " + preMemUsg);
			}
			
		}
		{ // scoped it to allow GC to free the heap allocated during this block
			Map<K, V> result = this.cacheBuilder.build(keys);
			if (result != null) {
				this.cache.putAll(result);
				for (K key : result.keySet()) {
					this.hitStats.putIfAbsent(key, new AtomicLong(0));
				}
			}
		}
		if (MemoryUsage.isEnabled()) {
			long postMemUsg = MemoryUsage.getMemoryUsage();
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "%%%%%%%%%%%%%%%% Memory usage post-init keys: " + Arrays.asList(keys)
						+ " is: " + postMemUsg + " (delta: " + (postMemUsg - preMemUsg) + ")");
			}
			
		}
	}
	
	/**
	 * Provides the keys currently present in the cache. 
	 * @return the set of keys (protected against modifications). 
	 */
	public Set<K> getKeys() {
		return Collections.<K>unmodifiableSet(this.cache.keySet());
	}

    /**
     * Retrieve the value for the given key - build it and store it into the cache if not already present. 
     * @param key the key identifying the value to be retrieved. 
     * @return the value from the cache (built on-demand, if not already present).
     * @throws CacheBuildingException wrapping an eventual problem occurred during the building process.
     */
    public V get(K key) throws CacheBuildingException {
        return get(key, false);
    }
    
	/**
	 * Retrieve the value for the given key - build it and store it into the cache if not already present. 
	 * @param key the key identifying the value to be retrieved. 
	 * @return the value from the cache (built on-demand, if not already present).
	 * @throws CacheBuildingException wrapping an eventual problem occurred during the building process.
	 */
	public V get(K key, boolean forceRebuild) throws CacheBuildingException {
		if (key == null) {
			throw new NullPointerException();
		}
		V value = null;
		if (!forceRebuild) {
		    value = this.cache.get(key);
		}
		if (value == null) {
			long preMemUsg = 0L;
			if (MemoryUsage.isEnabled()) {
				preMemUsg = MemoryUsage.getMemoryUsage();
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.log(Level.FINE, "%%%%%%%%%%%%%%%% Memory usage pre-building key '" +
							key + "' is: " + preMemUsg);
				}
				
			}
			{
				K[] keyArr = ObjectUtils.<K>createSingleArray(key);
				Map<K, V> oneKeyValueMap = this.cacheBuilder.build(keyArr);
				assert oneKeyValueMap == null || oneKeyValueMap.size() == 1 
						: "Cache Builder did not return a one-element map, but " + oneKeyValueMap;
				if (oneKeyValueMap != null) {
					assert oneKeyValueMap.containsKey(key) : "Returned map doesn't contain the given key " + key;
					value = oneKeyValueMap.get(key);
					cache.putAll(oneKeyValueMap);
					// note: a concurrent thread requesting the key here would have its cache hit cancelled right away
					this.hitStats.put(key, new AtomicLong(0));
				}
			}
			if (MemoryUsage.isEnabled()) {
				final long postMemUsg = MemoryUsage.getMemoryUsage();
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.log(Level.FINE, "%%%%%%%%%%%%%%%% Memory usage post-building key: '" + key + "' is: "
							+ postMemUsg + " (delta: " + (postMemUsg - preMemUsg) + ")");
				}
				
			}
		} else {
			putHitStatsIfAbsent(key, 0L).incrementAndGet();
			return value;
		}
		return value;
	}

	/**
	 * @param key
	 */
	private AtomicLong putHitStatsIfAbsent(K key, long l) {
		AtomicLong statObj = this.hitStats.get(key);
		if (statObj == null) {
			statObj = new AtomicLong(l);
			this.hitStats.put(key, statObj);
			return statObj;
		}
		return statObj;
	}
	
	public long getStat(K key) {
		return putHitStatsIfAbsent(key, 0L).get();
	}
	
	/**
	 * Nuke 'em all - clear all cache entries.  
	 */
	public Set<K> clearAll() {
		Set<K> keySet = new HashSet<K>(cache.keySet());
		this.cache.clear();
		this.hitStats.clear();
		
		return keySet;
	}
	
	/**
	 * Tells you how you should call it. 
	 * @return the cache name. 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Interface laying the contract for building a cache entry from a key.  
	 * 
	 * @param <K> the generic type of the cache key.
	 * @param <V> the generic type of the value. 
	 */
	public static interface CacheBuilder<K, V> {

		/**
		 * Builds the cache value for a given key.
		 * 
		 * @param keys the key to build the value for. 
		 * @return the built value. 
		 * @throws CacheBuildingException for problems encountered during building. 
		 */
		public java.util.Map<K, V> build(K... keys) throws CacheBuildingException;
	}
	
	/**
	 * Exception thrown by a cache builder for problems encountered during the building process.
	 * Should wrap the actual exception underneath. 
	 */
	@SuppressWarnings("serial")
	public static class CacheBuildingException extends Exception {

		public CacheBuildingException(String message) {
			super(message);
		}

		public CacheBuildingException(Throwable cause) {
			super(cause);
		}

		public CacheBuildingException(String message, Throwable cause) {
			super(message, cause);
		}

	}
	
}

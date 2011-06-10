/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.utils.ObjectUtils;

/**
 * Poke-enabled cache - adds the ability to "poke" the cache (i.e. invalidate it), 
 * as well as periodic refreshing.  
 * 
 * @author mpoplacenel
 */
public abstract class AbstractPokerCache<K, V> extends AbstractCache<K, V> implements PokerCache {
	
	public static final String POKER_PROPERTY_NAME = "poker";
	
	private static final SimpleDateFormat DFMT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
	
	/**
	 * Class logger. 
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractPokerCache.class.getName());
	
    /**
     * Flag ("face") to keep track of the poking: <code>true</code> if the cache was poked and 
     * needs to be rebuilt, and <code>false</code> otherwise.
     * Please don't go all ga-ga on them names. ;)
     */
    private AtomicBoolean pokerFace = new AtomicBoolean(true);
    
	private List<PokerCacheListener> listeners = new ArrayList<PokerCacheListener>();
    
    /**
     * Tracks the refresh status - queried to determine if the cache has timed out and needs
     * refreshing on the current thread. 
     */
    private RefreshTracker refreshTracker;
    
	/**
	 * Bare-bone constructor. Cache will be initialized with no auto-refresh. 
	 * @param name cache name.
	 * @param cacheBuilder cache builder.
	 */
	public AbstractPokerCache(String name, CacheBuilder<K, V> cacheBuilder) {
		this(name, cacheBuilder, 0, 0);
	}
	
	/**
	 * Full constructor - cache will be initialized with the given auto-refresh parameters. 
	 * @param name cache name.
	 * @param cacheBuilder cache builder.
	 * @param refreshInterval number of milliseconds specifying how often the cache should be
	 * refreshed (rebuilt). 
	 * @param startTimeOfDay seconds since midnight, specifying what time of the day the refresh
	 * counter should start. 
	 */
	public AbstractPokerCache(String name, CacheBuilder<K, V> cacheBuilder, long refreshInterval, int startTimeOfDay) {
		super(name, cacheBuilder);
		this.refreshTracker = new RefreshTracker(refreshInterval, startTimeOfDay);
	}
	
	public void init(K... keys) throws CacheBuildingException {
		// set the poker face to false, so other threads no longer try to build it.
		// They will still be able to build for an individual key, just like
		// during the initialization in the super scenario.
		boolean didSet = this.pokerFace.compareAndSet(true, false);
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, didSet ? "Covered the poker face" : "Poker face was already covered");
		}
		if (didSet) {
			for (PokerCacheListener l : this.listeners) {
				l.pokerOff(this);
			}
		}

		super.init(keys);
	}

	/**
	 * Check if the cache needs to be rebuilt, before actually serving the request. 
	 * @see org.ebayopensource.turmeric.utils.cache.AbstractCache#get(java.lang.Object)
	 */
	@Override
	public V get(K key) throws CacheBuildingException {
		try {
			checkCache(true);
		} catch (Throwable thr) {
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.log(Level.SEVERE, "Error rebuilding the cache", thr);
			}
		}
		return super.get(key);
	}
	
	public boolean poke() {
		return poke(false);
	}

	/**
	 * If it's false, set it to true and return true; else return false.
	 * @return true if poked was false, false if the setting was a no-op.
	 */
	public boolean poke(boolean rebuild) {
		boolean ret = this.pokerFace.compareAndSet(false, true);
		if (rebuild) {
			try {
				// we need to refresh because of the poker - don't care about the scheduled one
				// (note the refresh may actually be performed by another thread if this one gets suspended here)
				checkCache(false);
			} catch (Throwable thr) {
				if (LOGGER.isLoggable(Level.SEVERE)) {
					LOGGER.log(Level.SEVERE, "Error rebuilding the cache", thr);
				}
			}
		}
		
		return ret;
	}
	
	public boolean isPoker() {
		return this.pokerFace.get();
	}
	
	@Override
	public void addPokerCacheListener(PokerCacheListener l) {
		listeners.add(l);
	}

	@Override
	public void removePokerCacheListener(PokerCacheListener l) {
		this.listeners.remove(l);
	}

    /**
     * Checks if the cache needs to be rebuilt, either due to having been poked or 
     * due to its "freshness" having timed out, and proceed with rebuilding it
     * if that was the case.
     * 
     *  @param <code>true</code> to also check the refresh schedule, <code>false</code> otherwise.
     * 
     * @throws CacheBuildingException
     */
    protected void checkCache(boolean checkTimedSchedule) throws CacheBuildingException {
    	boolean refreshInit = checkTimedSchedule && checkCacheRefresh();
	    boolean pokeInit = this.pokerFace.compareAndSet(true, false);
	    if (pokeInit) {
	    	for (PokerCacheListener l : this.listeners) {
				l.pokerOff(this);
			}
	    }
	    // if shouldInit was true, doInit will be true; 
	    // if shouldInit was false, we ain't
		if (pokeInit || refreshInit) {
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "Rebuilding as poker = " + pokeInit + ", refresh = " + refreshInit);
			}
	    	rebuild();
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Rebuilt " + getKeys());
			}
	    }
    }
    
    protected boolean onlyClearOnRebuilding() {
    	return false;
    }

	/**
	 * Rebuild the cache for the current set of keys. 
	 * 
	 * @throws CacheBuildingException for cache building trouble. 
	 */
	protected void rebuild() throws CacheBuildingException {
		Set<K> keySet = clearAll();
		if (onlyClearOnRebuilding() || keySet.size() == 0) { // nothing to rebuild
			return;
		}
		K[] keys = ObjectUtils.createArray(keySet);
		super.init(keys);
	}
	
	/**
	 * Check if the cache needs to be refreshed. 
	 */
	protected boolean checkCacheRefresh() {
		return this.refreshTracker.isRefreshNeeded();
	}
    
    /**
     * Encapsulates the refresh logic - incrementing the last refresh time-stamp
     * with the given last refresh interval, and specifying when a refresh is necessary. 
     */
    protected static class RefreshTracker {
    	
    	/**
    	 * Refresh interval in milliseconds.
    	 */
    	private final long refreshInterval;
    	
        /**
         * Time-stamp of last refresh, as a java.util.Date.
         */
        private Date lastRefreshDate;
        
        private ReadWriteLock lock = new ReentrantReadWriteLock();
        
        /**
		 * @param refreshInterval
		 * @param startTimeOfDay
		 */
		public RefreshTracker(long refreshInterval, int startTimeOfDay) {
			super();
			
			this.refreshInterval = refreshInterval;
			initLastRefresh(startTimeOfDay);
		}
		
		/**
		 * Check if the cache needs to be refreshed. 
		 * @return <code>true</code> if a refresh is necessary, <code>false</code> otherwise.
		 */
		public boolean isRefreshNeeded() {
			this.lock.readLock().lock();
			try {
				if (!isUpdateNeeded()) {
					return false;
				}
			} finally {
				this.lock.readLock().unlock();
			}
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Refresh was deemed necessary");
			}
			// at this point, other threads may still reset the refreshInterval to 0,
			// or just increment the variables as part of another isRefreshNeeded() call.
			// so we need to check again.
			this.lock.writeLock().lock();
			try {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.log(Level.FINE, "Check refresh with write lock now");
				}
				if (isUpdateNeeded()) {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, "Refresh still seems necessary");
					}
					updateLastRefresh();
					return true;
				}
				
				return false;
			} finally {
				this.lock.writeLock().unlock();
			}
		}
		
		private boolean isUpdateNeeded() {
			if (this.refreshInterval == 0) {
				return false;
			}
			long currTime = Calendar.getInstance().getTimeInMillis();
			if (currTime > this.lastRefreshDate.getTime() + this.refreshInterval) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.log(Level.FINE, "Refresh determined as currTime = " 
							+ DFMT.format(new Date(currTime)) + " (" + currTime + ")"
							+ " > (last = " + DFMT.format(this.lastRefreshDate) + " (" + this.lastRefreshDate.getTime() 
							+ ")) + (interval = " + this.refreshInterval + ")");
				}
				
				return true;
			}
			return false;
		}

		/**
		 * Initialize the {@link #lastRefreshDate} variable based on the current time,
		 * refresh interval and start time of the day.
		 * @param startTimeOfDay the number of seconds since last midnight to set {@link #lastRefreshDate}
		 * to. 
		 * 
		 * <strong>Note:</strong> Do <strong>*NOT*</strong> call this other than from the constructor. 
		 */
		private void initLastRefresh(int startTimeOfDay) {
			if (this.refreshInterval <= 0) { // nothing to initialize
				this.lastRefreshDate = null;
				return;
			}
			try {
				Calendar c = Calendar.getInstance();
				if (startTimeOfDay >= 0) {
					// set to midnight current day
					c.set(Calendar.HOUR_OF_DAY, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);
					this.lastRefreshDate = new Date(c.getTimeInMillis() + startTimeOfDay * 1000);
				} else { // testing branch
					this.lastRefreshDate = new Date();
				}
			} catch (Exception e) {
				if (LOGGER.isLoggable(Level.SEVERE)) {
					LOGGER.log(Level.SEVERE, "Error encountered during calculating last refresh", e);
				}
			} catch (Throwable th){
				if (LOGGER.isLoggable(Level.SEVERE)) {
					LOGGER.log(Level.SEVERE, "Error encountered during calculating last refresh", th);
				}
			}
		}
	    
		/**
		 * Increment the {@link #lastRefreshDate} in {@link #refreshInterval} chunks
		 * until it gets into the future. 
		 */
		private void updateLastRefresh() {
			long currTime = Calendar.getInstance().getTimeInMillis();
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Current time = " + DFMT.format(new Date(currTime)) 
						+ ", lastRefreshDate = " + DFMT.format(this.lastRefreshDate));
			}
        	long lastRefreshTime = this.lastRefreshDate.getTime();
        	while (lastRefreshTime + this.refreshInterval < currTime) {
        		lastRefreshTime += this.refreshInterval;
        	}
        	this.lastRefreshDate = new Date(lastRefreshTime);
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "Last refresh time updated to " + DFMT.format(this.lastRefreshDate) 
						+ " (" + lastRefreshTime + " mSecs)");
			}
        }
        
    }
    
}

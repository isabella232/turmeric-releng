/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cache;

/**
 * This interface defines a cache that implements a poker behavior. 
 * @author mpoplacenel
 */
public interface PokerCache {
	
	/**
	 * Getter for the name of the poker. 
	 * @return its one, true identity. 
	 */
	String getName();
	
	/**
	 * Sets the poker on without clearing the cache.
	 * 
	 * @return <code>true</code> if the poker was off, 
	 * <code>false</code> if the poker was already on. 
	 */
	boolean poke();
	
	/**
	 * Sets the poker on, and clears the cache if that's also requested.
	 * @param rebuild <code>true</code> for also rebuilding the cache, 
	 * <code>false</code> to only set the poker. 
	 * 
	 * @return <code>true</code> if the poker was off, 
	 * <code>false</code> if the poker was already on. 
	 */
	boolean poke(boolean rebuild);
	
	/**
	 * Check the status of the poker. 
	 * @return <code>true</code> if the poker is on, <code>false</code> otherwise. 
	 */
	boolean isPoker();

	public void addPokerCacheListener(PokerCacheListener l);
	
	public void removePokerCacheListener(PokerCacheListener l);
	
	public static interface PokerCacheListener {
		
		void pokerOn(PokerCache source);
		
		void pokerOff(PokerCache source);
		
	}
	
}

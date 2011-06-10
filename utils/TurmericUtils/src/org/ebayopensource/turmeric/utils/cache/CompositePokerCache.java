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
import java.util.Collection;
import java.util.Collections;

/**
 * Composite pattern implementation for the poker cache - make multiple poker caches
 * look (and act) like one. 
 * 
 * @author mpoplacenel
 */
public class CompositePokerCache implements PokerCache {
	
	private final String name;
	
	private final Collection<PokerCache> pokerCaches;

	/**
	 * @param pokerCacheList
	 */
	public CompositePokerCache(String name, PokerCache... pokerCaches) {
		super();
		
		this.name = name;
		this.pokerCaches = Collections.unmodifiableCollection(Arrays.asList(pokerCaches));
	}

	@Override
	public void addPokerCacheListener(PokerCacheListener l) {
		for (PokerCache pokerCache : this.pokerCaches) {
			pokerCache.addPokerCacheListener(l);
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Combines the poker flags of the caches in the composition
	 * by a logical <code>AND</code>.
	 * 
	 * @return <code>true</code> if all caches have the poker on, 
	 * <code>false</code> otherwise. 
	 * 
	 * @see org.ebayopensource.org.ebayopensource.turmeric.utils.cache.PokerCache#isPoker()
	 */
	@Override
	public boolean isPoker() {
		boolean poker = true;
		for (PokerCache pokerCache : this.pokerCaches) {
			if (!pokerCache.isPoker()) poker = false;
		}
		return poker;
	}

	@Override
	public boolean poke() {
		return poke(false);
	}

	/**
	 * Pokes all caches in the composition and returns a logical <code>OR</code>
	 * of their return values.
	 * @return <code>true</code> if all caches were off, <code>false</code>
	 * if at least one was already on. 
	 * @see org.ebayopensource.org.ebayopensource.turmeric.utils.cache.PokerCache#poke(boolean)
	 */
	@Override
	public boolean poke(boolean rebuild) {
		boolean poker = false;
		for (PokerCache pokerCache : this.pokerCaches) {
			if (pokerCache.poke(rebuild)) poker = true;
		}
		
		return poker;
	}

	@Override
	public void removePokerCacheListener(PokerCacheListener l) {
		for (PokerCache pokerCache : this.pokerCaches) {
			pokerCache.removePokerCacheListener(l);
		}
	}

}

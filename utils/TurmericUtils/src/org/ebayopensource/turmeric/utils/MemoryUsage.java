/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Memory management and monitoring utilities.
 * 
 * @author mpoplacenel
 */
public class MemoryUsage {
	
	private static final Logger LOGGER = LogManager.getLogManager().getLogger(MemoryUsage.class.getName());
	
	private MemoryUsage() {
		// no instance - static only
	}

	/**
	 * Thread sleeps 4 x this when {@link #getMemoryUsage()} is called. Current value is 100. 
	 */
	public static final long SLEEP_INTERVAL = 100;
	
	public static final String MEMORY_USAGE_SYS_PROP = 
		"com.ebay.services.authorizationservice.impl.util.MemoryUsage.enabled";
	
	private static final boolean ENABLED;
	
	static {
		boolean tmpMemStatEnabled = false;
		try {
			tmpMemStatEnabled = Boolean.valueOf(System.getProperty(MEMORY_USAGE_SYS_PROP));
		} catch (Throwable thr) {
			LOGGER.log(Level.WARNING, "Could not read system property " + MEMORY_USAGE_SYS_PROP + " as boolean", thr);
		}
		ENABLED = tmpMemStatEnabled;
	}
	
	public static boolean isEnabled() {
		return ENABLED;
	}

	/**
	 * Returns the current memory usage statistic.
	 * @return {@link Runtime#totalMemory() - Runtime#freeMemory()}.
	 */
	public static long getMemoryUsage() {
		doGarbageDuty();
		long totalMemory = Runtime.getRuntime().totalMemory();

		doGarbageDuty();
		long freeMemory = Runtime.getRuntime().freeMemory();

		return totalMemory - freeMemory;
	}

	/**
	 * Collect the trash twice.
	 */
	private static void doGarbageDuty() {
		unlittering();
		unlittering();
	}

	/**
	 * Remove the litter, take a nap, finalizers wrap-up, nap again. 
	 */
	private static void unlittering() {
		try {
//			System.gc(); //KEEPME
			Thread.sleep(SLEEP_INTERVAL);
//			System.runFinalization(); //KEEPME
			Thread.sleep(SLEEP_INTERVAL);
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING, "Thread interrupted while cleaning up the garbage", e);
		}
	}
}
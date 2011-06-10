/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classic timer implementation, to be used in performance instrumentation. 
 * <p>
 * A typical use-case is:
 * <pre>
 *     Timer timer = null;
 *     if (LOGGER.isDebugEnabled()) {
 *         timer = new Timer("executeMethod"); // starts automatically
 *         LOGGER.debug(timer.toString());
 *     }
 *     executeMethod();
 *     if (LOGGER.isDebugEnabled()) {
 *         long delta = Timer.end();
 *         LOGGER.debug(timer.toString);
 *     }
 * </pre>      
 * 
 * @author mpoplacenel
 */
public class Timer {
	
	private final String name;
	
	private final Date startTime;
	
	private Date endTime;
	
	private Long delta;

	/**
	 * This is non-static only to avoid its initialization when the object is only declared,
	 * such as in production environments. 
	 */
	private final DateFormat dfmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
	
	/**
	 * Constructor.
	 */
	public Timer(String name) {
		this.name = name;
		this.startTime = new Date();
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public String getStartTimePretty() {
		return this.dfmt.format(this.startTime);
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public String getEndTimePretty() {
		return this.dfmt.format(this.endTime);
	}

	public long end() {
		this.endTime = new Date();
		this.delta = this.endTime.getTime() - this.startTime.getTime();
		return this.delta;
	}
	
	public long getDelta() {
		if (this.delta == null) {
			end();
		}
		return this.delta;
	}
	
	public String toString() {
		return this.name + " started at " + getStartTimePretty()
			+ (this.delta == null ? "" 
				: ", ended at " + getEndTimePretty() + " after " + this.delta + " mSecs");
	}

}
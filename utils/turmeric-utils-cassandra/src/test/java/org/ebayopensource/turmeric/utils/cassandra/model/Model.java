/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.model;

import java.util.Date;

/**
 * The Class Model.
 * @author jamuguerza
 */
public class Model {
	
	/** The key. */
	private String key;
	
	/** The time data. */
	private Date timeData;
	
	/** The boolean data. */
	private boolean booleanData;
	
	/** The string data. */
	private String stringData;
	
	/** The int data. */
	private int intData;
	
	/** The long data. */
	private Long longData;
	
	/**
	 * Checks if is boolean data.
	 *
	 * @return true, if is boolean data
	 */
	public boolean isBooleanData() {
		return booleanData;
	}

	/**
	 * Sets the boolean data.
	 *
	 * @param booleanData the new boolean data
	 */
	public void setBooleanData(boolean booleanData) {
		this.booleanData = booleanData;
	}

	/**
	 * Gets the string data.
	 *
	 * @return the string data
	 */
	public String getStringData() {
		return stringData;
	}

	/**
	 * Sets the string data.
	 *
	 * @param stringData the new string data
	 */
	public void setStringData(String stringData) {
		this.stringData = stringData;
	}

	/**
	 * Gets the int data.
	 *
	 * @return the int data
	 */
	public int getIntData() {
		return intData;
	}

	/**
	 * Sets the int data.
	 *
	 * @param intData the new int data
	 */
	public void setIntData(int intData) {
		this.intData = intData;
	}

	/**
	 * Sets the time data.
	 *
	 * @param timeData the new time data
	 */
	public void setTimeData(Date timeData) {
		this.timeData = timeData;
	}

	/**
	 * Gets the time data.
	 *
	 * @return the time data
	 */
	public Date getTimeData() {
		return timeData;
	}

	/**
	 * Gets the long data.
	 *
	 * @return the long data
	 */
	public Long getLongData() {
		return longData;
	}

	/**
	 * Sets the long data.
	 *
	 * @param longData the new long data
	 */
	public void setLongData(Long longData) {
		this.longData = longData;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
}

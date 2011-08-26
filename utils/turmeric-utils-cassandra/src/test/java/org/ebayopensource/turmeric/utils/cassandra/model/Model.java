package org.ebayopensource.turmeric.utils.cassandra.model;

import java.util.Date;

public class Model {
	private String key;
	private Date timeData;
	private boolean booleanData;
	private String stringData;
	private int intData;
	private Long longData;
	
	public boolean isBooleanData() {
		return booleanData;
	}

	public void setBooleanData(boolean booleanData) {
		this.booleanData = booleanData;
	}

	public String getStringData() {
		return stringData;
	}

	public void setStringData(String stringData) {
		this.stringData = stringData;
	}

	public int getIntData() {
		return intData;
	}

	public void setIntData(int intData) {
		this.intData = intData;
	}

	public void setTimeData(Date timeData) {
		this.timeData = timeData;
	}

	public Date getTimeData() {
		return timeData;
	}

	public Long getLongData() {
		return longData;
	}

	public void setLongData(Long longData) {
		this.longData = longData;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}

package org.slf4j.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * Factory for fetching and creating {@link JulLogger} instances.
 * A simple cache of past named loggers is maintained.
 */
public class JulLoggerFactory implements ILoggerFactory {
	/** Required by slf4j api */
	final static JulLoggerFactory INSTANCE = new JulLoggerFactory();
	
	private Map<String,Logger> loggerMap = new HashMap<String,Logger>();
	
	@Override
	public Logger getLogger(String name) {
		Logger logr = null;
		
		synchronized(this)
		{
			logr = loggerMap.get(name);
			if(logr == null) {
				logr = new JulLogger(name);
				loggerMap.put(name, logr);
			}
		}
		
		return logr;
	}
}

package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * Main entry point for slf4j, how it binds slf4j-to-jul to the active slf4j instance.
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
	/** Required by Slf4j API */
	public static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
	private static final String loggerFactoryClassStr = JulLoggerFactory.class.getName();
	private final ILoggerFactory loggerFactory;
	
	public static StaticLoggerBinder getSingleton() {
		return SINGLETON;
	}

	private StaticLoggerBinder() {
		loggerFactory = new JulLoggerFactory();
	}

	@Override
	public ILoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	@Override
	public String getLoggerFactoryClassStr() {
		return loggerFactoryClassStr;
	}
}

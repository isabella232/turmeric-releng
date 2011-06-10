package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * Default {@link MarkerFactoryBinder} implementation.
 */
public class StaticMarkerBinder implements MarkerFactoryBinder {
	/** Required by Slf4j API */
	public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
	private final IMarkerFactory markerFactory = new BasicMarkerFactory();

	@Override
	public IMarkerFactory getMarkerFactory() {
		return markerFactory;
	}

	@Override
	public String getMarkerFactoryClassStr() {
		return BasicMarkerFactory.class.getName();
	}
}

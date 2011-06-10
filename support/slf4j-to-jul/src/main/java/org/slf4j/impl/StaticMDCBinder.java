package org.slf4j.impl;

import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

/**
 * Default {@link MDCAdapter} bound to {@link NOPMakerAdapter}
 */
public class StaticMDCBinder {
	/** Required by Slf4j API */
	public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();
	public MDCAdapter getMDCA() {
		return new NOPMDCAdapter();
	}
	
	public String getMDCAdapterClassStr() {
		return NOPMDCAdapter.class.getName();
	}
}

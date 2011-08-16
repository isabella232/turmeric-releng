/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class HectorManagetTest {
	@BeforeClass
	public static void setup() {

	}

	@Test
	public void testInsert() {
		HectorManager.insert("RLCounters", "activeEffect", "subject_test_001",
				"ip", "200.100.100.200");
	}

	@Test
	public void testGetColumn() {
		String value = HectorManager.get("RLCounters", "activeEffect", "ip",
				"subject_test_001");
		Assert.assertNotNull(value);
		Assert.assertEquals("200.100.100.200", value);
	}

	@Test
	public void testGetFromAnotherHost() {

		// TODO - Create a HectorManager mock and override the getHost to retrieve
		// 192.168.2.202 then continue testing
		// String value = HectorManager.get("RLCounters","activeEffect", "ip",
		// "subject_test_001");
		// Assert.assertNotNull(value);
		// Assert.assertEquals("200.100.100.203",value);
	}

}

package org.ebayopensource.turmeric.utils.cassandra;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class HectorManagetTest {
	@BeforeClass
	public static void setup() {
		//Keyspace keyspace = HectorManager.getKeyspace("RLCounters");
	}

	@Test//(expected=Exception.class)
	public void testInsert() {
		HectorManager.insert("activeEffect", "subject_002", "ip",
				"200.100.100.200");
		HectorManager.insert("activeEffect", "subject_002", "ip",
		"200.100.100.202");
	}

	@Test
	public void testGetColumn() {
		String value = HectorManager.get("activeEffect", "ip", "subject_002");
		Assert.assertNotNull(value);
		Assert.assertEquals("200.100.100.203",value);
	}

}

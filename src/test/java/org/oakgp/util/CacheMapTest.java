package org.oakgp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class CacheMapTest {
	@Test
	public void testSynchronized() {
		assertEquals("java.util.Collections$SynchronizedMap", CacheMap.createCache(3).getClass().getName());
	}

	@Test
	public void testSizeThree() {
		final int maxSize = 3;
		Map<String, Integer> m = CacheMap.createCache(maxSize);

		m.put("a", 1);
		m.put("b", 1);
		m.put("c", 1);
		m.put("d", 1);
		m.put("e", 1);

		assertEquals(maxSize, m.size());
		assertTrue(m.containsKey("c"));
		assertTrue(m.containsKey("d"));
		assertTrue(m.containsKey("e"));

		m.put("a", 1);

		assertEquals(maxSize, m.size());
		assertTrue(m.containsKey("a"));
		assertTrue(m.containsKey("d"));
		assertTrue(m.containsKey("e"));

		m.get("d");
		m.put("x", 1);
		m.get("d");
		m.put("y", 1);
		m.get("d");
		m.put("z", 1);

		assertEquals(maxSize, m.size());
		assertTrue(m.containsKey("d"));
		assertTrue(m.containsKey("y"));
		assertTrue(m.containsKey("z"));

		m.get("y");
		m.get("z");
		m.put("a", 1);
		m.get("y");
		m.get("z");
		m.put("b", 1);
		m.get("y");
		m.get("z");
		m.put("x", 1);

		assertEquals(maxSize, m.size());
		assertTrue(m.containsKey("x"));
		assertTrue(m.containsKey("y"));
		assertTrue(m.containsKey("z"));

		m.put("q", 1);
		m.put("r", 1);
		m.put("y", 1);

		assertEquals(maxSize, m.size());
		assertTrue(m.containsKey("q"));
		assertTrue(m.containsKey("r"));
		assertTrue(m.containsKey("y"));
	}

	@Test
	public void testSizeTen() {
		final int maxSize = 10;
		Map<Integer, String> m = CacheMap.createCache(maxSize);

		for (int i = 0; i <= maxSize; i++) {
			m.put(i, "");
		}
		assertEquals(maxSize, m.size());
		assertFalse(m.containsKey(0));

		for (int i = 0; i <= maxSize; i++) {
			m.put(i, "");
			m.get(0);
		}
		assertEquals(maxSize, m.size());
		assertTrue(m.containsKey(0));
	}
}
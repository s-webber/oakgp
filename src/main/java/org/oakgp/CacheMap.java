package org.oakgp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CacheMap<K, V> extends LinkedHashMap<K, V> {
	public static <K, V> Map<K, V> createCache(int maxSize) {
		CacheMap<K, V> m = new CacheMap<>(maxSize);
		return Collections.synchronizedMap(m);
	}

	private final int maxSize;

	private CacheMap(int maxSize) {
		super(maxSize, 0.1f, true);
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
}

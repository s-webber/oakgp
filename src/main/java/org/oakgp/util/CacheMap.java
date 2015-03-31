package org.oakgp.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a size-limited map of keys to values.
 * <p>
 * When the maximum size limit has been reached, the least-recently accessed entry will be removed whenever a new entry is added.
 *
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
public final class CacheMap<K, V> extends LinkedHashMap<K, V> {
	private final int maxSize;

	/**
	 * Returns a size-limited map of keys to values.
	 *
	 * @param maxSize
	 *            the maximum size restriction to enforce on the returned map
	 * @param <K>
	 *            the type of keys maintained by this map
	 * @param <V>
	 *            the type of mapped values
	 * @return a size-limited map of keys to values
	 */
	public static <K, V> Map<K, V> createCache(int maxSize) {
		CacheMap<K, V> m = new CacheMap<>(maxSize);
		return Collections.synchronizedMap(m);
	}

	/** @see {@link #createCache(int)} */
	private CacheMap(int maxSize) {
		super(maxSize, 0.1f, true);
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
}

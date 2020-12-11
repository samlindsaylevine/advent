package advent.utils

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache

/**
 * Make a Guava LoadingCache based on a function that doesn't accept nulls.
 *
 * Handles the slightly unpleasant interop with Java implicitly-nullable types.
 */
fun <K, V> loadingCache(calculation: (K) -> V): LoadingCache<K, V> = CacheBuilder.newBuilder()
        .build(CacheLoader.from { key: K? -> calculation(key!!) })
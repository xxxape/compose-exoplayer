package com.xxxape.exoplayer.cache

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

/**
 * 缓存实例：使用 Media3 SimpleCache，供 [VideoDataSourceHolder] 使用。
 */
@UnstableApi
object CacheHolder {
    private var cache: SimpleCache? = null
    private val lock = Any()

    fun get(context: Context): SimpleCache {
        synchronized(lock) {
            if (cache == null) {
                val cacheDir = File(context.cacheDir, "exoplayer_cache")
                val cacheSize = 20L * 1024 * 1024 // 20MB
                val evictor = LeastRecentlyUsedCacheEvictor(cacheSize)
                val databaseProvider = StandaloneDatabaseProvider(context)
                cache = SimpleCache(cacheDir, evictor, databaseProvider)
            }
        }
        return cache!!
    }
}

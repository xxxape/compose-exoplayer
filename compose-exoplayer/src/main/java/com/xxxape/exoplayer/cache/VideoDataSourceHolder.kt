package com.xxxape.exoplayer.cache

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource

/**
 * 带缓存的 DataSource.Factory，供播放器根据 URL 类型构建 MediaSource 时使用。
 */
@UnstableApi
object VideoDataSourceHolder {
    private var cacheDataSourceFactory: CacheDataSource.Factory? = null
    @SuppressLint("StaticFieldLeak")
    private var defaultDataSourceFactory: DefaultDataSource.Factory? = null

    fun getCacheFactory(context: Context): CacheDataSource.Factory {
        if (cacheDataSourceFactory == null) {
            val cache = CacheHolder.get(context)
            val upstreamFactory = getDefaultFactory(context)
            cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setCacheReadDataSourceFactory(FileDataSource.Factory())
                .setCacheWriteDataSinkFactory(
                    CacheDataSink.Factory()
                        .setCache(cache)
                        .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE)
                )
        }
        return cacheDataSourceFactory!!
    }

    private fun getDefaultFactory(context: Context): DefaultDataSource.Factory {
        if (defaultDataSourceFactory == null) {
            val httpFactory = DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(context, context.packageName))
            defaultDataSourceFactory = DefaultDataSource.Factory(context, httpFactory)
        }
        return defaultDataSourceFactory!!
    }
}

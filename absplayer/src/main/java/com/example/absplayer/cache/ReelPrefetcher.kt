package com.example.absplayer.cache

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import com.example.absplayer.data.ReelItem

@OptIn(UnstableApi::class)
internal class ReelPrefetcher(private val context: Context) {
    private val cacheDataSourceFactory = VideoCacheManager.getCacheDataSourceFactory(context)
    private val prefetchJobs = ConcurrentHashMap<String, Job>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val PREFETCH_SIZE = 2 * 1024 * 1024L
    }

    fun prefetch(reels: List<ReelItem>, currentIndex: Int) {
        val rangeToKeep = (currentIndex - 1)..(currentIndex + 3)
        prefetchJobs.keys.forEach { id ->
            val index = reels.indexOfFirst { it.id == id }
            if (index !in rangeToKeep) {
                prefetchJobs[id]?.cancel()
                prefetchJobs.remove(id)
            }
        }

        for (i in 1..2) {
            val nextIndex = currentIndex + i
            if (nextIndex < reels.size) {
                val reel = reels[nextIndex]
                if (!prefetchJobs.containsKey(reel.id)) {
                    val job = coroutineScope.launch {
                        startPrefetch(reel.videoUrl, PREFETCH_SIZE)
                    }
                    prefetchJobs[reel.id] = job
                }
            }
        }
    }


    private suspend fun startPrefetch(videoUrl: String, length: Long) =
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(videoUrl)

                val dataSpec = DataSpec.Builder()
                    .setUri(uri)
                    .setLength(length)
                    .build()

                val cacheWriter = CacheWriter(
                    cacheDataSourceFactory.createDataSource(),
                    dataSpec,
                    null,
                    null
                )

                cacheWriter.cache()
            } catch (e: Exception) {
            }
        }

    fun cancelAll() {
        prefetchJobs.values.forEach { it.cancel() }
        prefetchJobs.clear()
    }
}
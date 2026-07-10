package com.example.absplayer

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.absplayer.cache.ReelPrefetcher
import com.example.absplayer.cache.ReelsPlayerPool
import com.example.absplayer.data.ReelItem
import com.example.absplayer.utility.ReelPlayerState

/**
 * Entry point for the AbsPlayer library.
 */
object AbsPlayer {
    private var pool: ReelsPlayerPool? = null
    private var prefetcher: ReelPrefetcher? = null

    /**
     * Initialize the library. Call this in your Application class or MainActivity.
     */
    fun init(context: Context) {
        if (pool == null) {
            pool = ReelsPlayerPool(context.applicationContext)
        }
        if (prefetcher == null) {
            prefetcher = ReelPrefetcher(context.applicationContext)
        }
    }

    internal fun getPool(): ReelsPlayerPool {
        return pool ?: throw IllegalStateException("AbsPlayer must be initialized before use. Call AbsPlayer.init(context).")
    }

    internal fun getPrefetcher(): ReelPrefetcher {
        return prefetcher ?: throw IllegalStateException("AbsPlayer must be initialized before use. Call AbsPlayer.init(context).")
    }


    @Composable
    fun ReelsViewer(
        reels: List<ReelItem>,
        modifier: Modifier = Modifier.Companion,
        overlay: @Composable (ReelItem, ReelPlayerState) -> Unit = { _, _ -> }
    ) {
        val currentPool = remember { getPool() }
        val currentPrefetcher = remember { getPrefetcher() }

        ReelsScreen(
            reels = reels,
            pool = currentPool,
            prefetcher = currentPrefetcher,
            modifier = modifier,
            overlay = overlay
        )
    }
}
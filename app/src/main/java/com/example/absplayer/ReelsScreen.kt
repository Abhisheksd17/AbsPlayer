package com.example.absplayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem

@Composable
fun ReelsScreen(
    reels: List<ReelItem>,
    pool: ReelsPlayerPool,
    prefetcher: ReelPrefetcher
) {
    val pagerState = rememberPagerState { reels.size }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Single player and state for the entire screen
    val player = remember { pool.acquire() }
    val reelState = remember(player) { ReelPlayerState(player) }

    // Manage lifecycle (Pause on background, Release on destroy)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    player.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    // Resume if needed
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.stop()
            prefetcher.cancelAll()
            // Note: We don't release here if the activity is just rotating, 
            // but for this simple setup we'll assume it's fine.
        }
    }

    // Update media item and trigger prefetch when the page changes
    LaunchedEffect(pagerState.currentPage) {
        val reel = reels[pagerState.currentPage]
        
        // 1. Update Player
        player.setMediaItem(MediaItem.fromUri(reel.videoUrl))
        player.prepare()
        player.play()
        
        // 2. Trigger Prefetch for upcoming videos
        prefetcher.prefetch(reels, pagerState.currentPage)
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 0 
    ) { page ->
        // Only attach the player to the view of the current page
        if (page == pagerState.currentPage) {
            ReelVideoPlayer(state = reelState, modifier = Modifier.fillMaxSize())
        } else {
            // Placeholder for non-active pages
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
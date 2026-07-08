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
internal fun ReelsScreen(
    reels: List<ReelItem>,
    pool: ReelsPlayerPool,
    prefetcher: ReelPrefetcher,
    modifier: Modifier = Modifier,
    overlay: @Composable (ReelItem, ReelPlayerState) -> Unit = { _, _ -> }
) {
    val pagerState = rememberPagerState { reels.size }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val player = remember { pool.acquire() }
    val reelState = remember(player) { ReelPlayerState(player) }

    LaunchedEffect(reels) {
        val mediaItems = reels.map { MediaItem.fromUri(it.videoUrl) }
        player.setMediaItems(mediaItems)
        player.prepare()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player.pause()
                Lifecycle.Event.ON_RESUME -> player.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.stop()
            prefetcher.cancelAll()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (player.mediaItemCount > pagerState.currentPage) {
            player.seekToDefaultPosition(pagerState.currentPage)
            player.play()
        }
        prefetcher.prefetch(reels, pagerState.currentPage)
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = 1 
    ) { page ->
        if (page == pagerState.currentPage) {
            ReelVideoPlayer(
                state = reelState, 
                modifier = Modifier.fillMaxSize(),
                overlay = { overlay(reels[page], reelState) }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

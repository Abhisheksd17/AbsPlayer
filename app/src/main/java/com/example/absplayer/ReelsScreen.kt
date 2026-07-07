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
fun ReelsScreen(reels: List<ReelItem>, pool: ReelsPlayerPool) {
    val pagerState = rememberPagerState { reels.size }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val player = remember { pool.acquire() }
    val reelState = remember(player) { ReelPlayerState(player) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    player.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.stop()
            player.release() 
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val reel = reels[pagerState.currentPage]
        player.setMediaItem(MediaItem.fromUri(reel.videoUrl))
        player.prepare()
        player.play()
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 0 
    ) { page ->
        if (page == pagerState.currentPage) {
            ReelVideoPlayer(state = reelState, modifier = Modifier.fillMaxSize())
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
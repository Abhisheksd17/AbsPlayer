package com.example.absplayer

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun ReelVideoPlayer(
    state: ReelPlayerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var controlsVisibleUntil by remember { mutableStateOf(0L) }

    LaunchedEffect(state.player) {
        state.startProgressLoop(scope)
    }

    // auto-hide controls after 2.5s of inactivity
    LaunchedEffect(controlsVisibleUntil) {
        if (controlsVisibleUntil > 0) {
            delay(2500)
            if (System.currentTimeMillis() >= controlsVisibleUntil) {
                state.showControls = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()

            // Tap gesture
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        state.togglePlayPause()
                        state.showControls = true
                        controlsVisibleUntil = System.currentTimeMillis() + 2500
                    }
                )
            }

            // Long press gesture
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()

                    // Ignore presses on the left side
                    if (down.position.x <= size.width / 2f) {
                        waitForUpOrCancellation()
                        return@awaitEachGesture
                    }

                    // Wait until it becomes a long press
                    val longPress =
                        awaitLongPressOrCancellation(down.id)

                    if (longPress != null) {
                        state.setSpeed(2f)

                        // Keep 2x until finger is lifted
                        waitForUpOrCancellation()

                        state.setSpeed(1f)
                    }
                }
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false // we draw our own
                    player = state.player
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Center pause icon, IG-style: fades in/out, doesn't block taps underneath
        androidx.compose.animation.AnimatedVisibility(
            visible = !state.isPlaying,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(72.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            ReelSeekBar(state = state)
            SpeedControlButton(state = state)
        }
    }
}


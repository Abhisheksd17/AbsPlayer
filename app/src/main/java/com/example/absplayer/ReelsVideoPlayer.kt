package com.example.absplayer

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
internal fun ReelVideoPlayer(
    state: ReelPlayerState,
    modifier: Modifier = Modifier,
    overlay: @Composable () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var controlsVisibleUntil by remember { mutableLongStateOf(0L) }

    LaunchedEffect(state.player) {
        state.startProgressLoop(scope)
    }

    // auto-hide controls after 2.5s
    LaunchedEffect(controlsVisibleUntil) {
        if (controlsVisibleUntil > 0L) {
            delay(2500L)
            if (System.currentTimeMillis() >= controlsVisibleUntil) {
                state.showControls = false
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Background for letterboxing
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        state.togglePlayPause()
                        state.showControls = true
                        controlsVisibleUntil = System.currentTimeMillis() + 2500L
                    }
                )
            }
            // Long Press on Right Side for 2x Speed
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    
                    // Only trigger on the right 50% of the screen
                    if (down.position.x > (size.width / 2f)) {
                        val longPress = awaitLongPressOrCancellation(down.id)
                        if (longPress != null) {
                            state.setSpeed(2f)
                            waitForUpOrCancellation()
                            state.setSpeed(1f)
                        }
                    }
                }
            }
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Calculate dynamic player dimensions to prevent cropping
        val videoModifier = if (state.videoWidth > 0 && state.videoHeight > 0) {
            // Formula: height = screenWidth * videoHeight / videoWidth
            var calculatedHeight = screenWidth * state.videoHeight / state.videoWidth
            
            // Cap to screen height
            if (calculatedHeight > screenHeight) {
                calculatedHeight = screenHeight
            }
            
            Modifier
                .width(screenWidth)
                .height(calculatedHeight)
                .align(Alignment.Center)
        } else {
            Modifier.fillMaxSize()
        }

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    player = state.player
                    // Explicitly using FIT to avoid cropping
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = videoModifier
        )

        // 2x Speed Overlay (IG Style)
        androidx.compose.animation.AnimatedVisibility(
            visible = state.playbackSpeed > 1f,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = "${state.playbackSpeed.toInt()}x Speed",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Center pause icon
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

        // Custom Overlay provided by the user
        Box(modifier = Modifier.fillMaxSize()) {
            overlay()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            ReelSeekBar(state = state)
            // SpeedControlButton is now optional or part of the library UI
            // SpeedControlButton(state = state)
        }
    }
}

package com.example.absplayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * State holder for a single Reel playback.
 * Exposed to allow custom overlays to interact with the player.
 */
class ReelPlayerState(val player: ExoPlayer) {
    var isPlaying by mutableStateOf(player.isPlaying)
    var currentPosition by mutableLongStateOf(0L)
    var duration by mutableLongStateOf(0L)
    var showControls by mutableStateOf(false)
    var playbackSpeed by mutableFloatStateOf(1f)

    var videoWidth by mutableIntStateOf(0)
    var videoHeight by mutableIntStateOf(0)

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onEvents(p: Player, events: Player.Events) {
                duration = p.duration.coerceAtLeast(0L)
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                videoWidth = videoSize.width
                videoHeight = videoSize.height
            }
        })
    }

    internal fun startProgressLoop(scope: CoroutineScope) {
        scope.launch {
            while (isActive) {
                currentPosition = player.currentPosition
                delay(200)
            }
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed
        player.setPlaybackParameters(PlaybackParameters(speed))
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }
}

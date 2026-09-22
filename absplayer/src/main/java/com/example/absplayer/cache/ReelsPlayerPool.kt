package com.example.absplayer.cache

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

@OptIn(UnstableApi::class)
internal class ReelsPlayerPool(context: Context) {
    private val cacheDataSourceFactory = VideoCacheManager.getCacheDataSourceFactory(context)

    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            15_000,
            50_000,
            500,
            1_000
        )
        .build()

    private val player = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
        .setLoadControl(loadControl)
        .build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }


    fun acquire(): ExoPlayer {
        return player
    }

    fun releaseAll() = player.release()
}
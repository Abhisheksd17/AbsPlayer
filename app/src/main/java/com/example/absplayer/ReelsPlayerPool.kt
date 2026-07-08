package com.example.absplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

@OptIn(UnstableApi::class)
class ReelsPlayerPool(context: Context) {
    private val cacheDataSourceFactory = VideoCacheManager.getCacheDataSourceFactory(context)
    
    private val player = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
        .build().apply {
            repeatMode = Player.REPEAT_MODE_ONE // reels loop
        }

    fun acquire(): ExoPlayer {
        return player
    }

    fun releaseAll() = player.release()
}
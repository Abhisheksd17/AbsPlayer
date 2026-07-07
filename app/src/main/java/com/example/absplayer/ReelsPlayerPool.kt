package com.example.absplayer

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ReelsPlayerPool(context: Context) {
    private val player = ExoPlayer.Builder(context).build().apply {
        repeatMode = Player.REPEAT_MODE_ONE // reels loop
    }

    fun acquire(): ExoPlayer {
        return player
    }

    fun releaseAll() = player.release()
}
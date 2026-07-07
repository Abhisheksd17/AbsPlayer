package com.example.absplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelSeekBar(state: ReelPlayerState) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableFloatStateOf(0f) }

    val progress = if (isDragging) dragPosition
    else if (state.duration > 0) state.currentPosition / state.duration.toFloat()
    else 0f

    Slider(
        value = progress.coerceIn(0f, 1f),
        onValueChange = {
            isDragging = true
            dragPosition = it
        },
        onValueChangeFinished = {
            val seekTo = (dragPosition * state.duration).toLong()
            state.player.seekTo(seekTo)
            isDragging = false
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isDragging) 16.dp else 8.dp)
            .padding(horizontal = 8.dp),
        colors = SliderDefaults.colors(
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
            thumbColor = Color.White
        ),
        thumb = {
            // Instagram hides the thumb until you touch it
            if (isDragging) {
                Box(
                    Modifier
                        .size(14.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    )
}
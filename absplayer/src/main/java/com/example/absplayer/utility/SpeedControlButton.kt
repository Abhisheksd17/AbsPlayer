package com.example.absplayer.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SpeedControlButton(state: ReelPlayerState) {
    var expanded by remember { mutableStateOf(false) }
    val speeds = listOf(0.5f, 1f, 1.5f, 2f)

    Box(modifier = Modifier.padding(horizontal = 12.dp)) {
        if (!expanded) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.clickable { expanded = true }
            ) {
                Text(
                    "${state.playbackSpeed}x",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 12.sp
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                speeds.forEach { speed ->
                    Text(
                        "${speed}x",
                        color = if (speed == state.playbackSpeed) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = if (speed == state.playbackSpeed) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable {
                                state.setSpeed(speed)
                                expanded = false
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
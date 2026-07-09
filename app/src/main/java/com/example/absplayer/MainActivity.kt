package com.example.absplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.absplayer.ui.theme.AbsPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        AbsPlayer.init(this)
        
        enableEdgeToEdge()
        setContent {
            AbsPlayerTheme {
                val reels = remember {
                    listOf(
                        ReelItem("1", "https://res.cloudinary.com/dujzbrfam/video/upload/v1774088557/gcxyj8d1unl99vcgizi5.mp4"),
                        ReelItem("2", "https://res.cloudinary.com/dujzbrfam/video/upload/v1774088591/hebl5ckcagftyqpsucr3.mp4"),
                        ReelItem("3", "https://res.cloudinary.com/dujzbrfam/video/upload/v1774088633/tis7dul2nuemzx9mxs5c.mp4"),
                        ReelItem("4", "https://res.cloudinary.com/dujzbrfam/video/upload/v1774088661/t2gnenjxloyqlg92naqk.mp4")
                    )
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AbsPlayer.ReelsViewer(
                        reels = reels,
                        modifier = Modifier.padding(innerPadding)
                    ) { reel, state ->

                    }
                }
            }
        }
    }
}

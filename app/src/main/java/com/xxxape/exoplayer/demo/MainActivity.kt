package com.xxxape.exoplayer.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xxxape.exoplayer.demo.ui.theme.ComposeexoplayerTheme
import com.xxxape.exoplayer.player.rememberMediaPlayerState
import com.xxxape.exoplayer.ui.RotatableMediaPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeexoplayerTheme {
                PlayerScreen()
            }
        }
    }
}

@Composable
private fun PlayerScreen(modifier: Modifier = Modifier) {
    val urlList = listOf(
        "https://cdncert.froglesson.com/upload/muscle/170385149087232750.mp4",
        "https://cdncert.froglesson.com/upload/muscle/170385150395410474.mp4",
        "https://cdncert.froglesson.com/upload/muscle/170385153380659491.mp4"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            urlList.forEachIndexed { index, url ->
                RotatableMediaPlayer(
                    playerState = rememberMediaPlayerState(playWhenReady = false, repeatMode = 0),
                    url = url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    cover = { VideoCover(Modifier.fillMaxSize()) }
                )
                Text(
                    text = "第 $index 个视频下方的文字",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun VideoCover(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Color.Black.copy(alpha = 0.5f))) {
        Text(
            text = "视频封面",
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

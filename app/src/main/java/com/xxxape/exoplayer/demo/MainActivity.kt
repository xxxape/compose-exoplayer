package com.xxxape.exoplayer.demo

import android.content.res.Configuration
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xxxape.exoplayer.demo.ui.theme.ComposeexoplayerTheme
import com.xxxape.exoplayer.player.MediaPlayerBackHandler
import com.xxxape.exoplayer.player.MediaPlayerState
import com.xxxape.exoplayer.player.rememberMediaPlayerState
import com.xxxape.exoplayer.ui.FullScreenPlayerOverlay
import com.xxxape.exoplayer.ui.SimpleMediaPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeexoplayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlayerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 仅当从全屏切回竖屏时同步状态（由点击「退出全屏」触发），不响应用户旋转手机
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            playerStates.forEach { it.exitFullscreen() }
        }
    }

    override fun onDestroy() {
        playerStates.clear()
        super.onDestroy()
    }

    companion object {
        val playerStates = mutableListOf<MediaPlayerState>()
    }
}

@Composable
private fun PlayerScreen(modifier: Modifier = Modifier) {
    val state1 = rememberMediaPlayerState(playWhenReady = false, repeatMode = 0)
    val state2 = rememberMediaPlayerState(playWhenReady = false, repeatMode = 0)
    val state3 = rememberMediaPlayerState(playWhenReady = false, repeatMode = 0)

    SideEffect {
        MainActivity.playerStates.clear()
        MainActivity.playerStates.add(state1)
        MainActivity.playerStates.add(state2)
        MainActivity.playerStates.add(state3)
    }

    val url1 = "https://cdncert.froglesson.com/upload/muscle/170385149087232750.mp4"
    val url2 = "https://cdncert.froglesson.com/upload/muscle/170385150395410474.mp4"
    val url3 = "https://cdncert.froglesson.com/upload/muscle/170385150395410474.mp4"

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SimpleMediaPlayer(
                playerState = state1,
                url = url1,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                cover = { VideoCover(Modifier.fillMaxSize()) }
            )
            Text(
                text = "第一个视频下方的文字",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            SimpleMediaPlayer(
                playerState = state2,
                url = url2,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                cover = { VideoCover(Modifier.fillMaxSize()) }
            )
            Text(
                text = "第二个视频下方的文字",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
            SimpleMediaPlayer(
                playerState = state3,
                url = url3,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                cover = { VideoCover(Modifier.fillMaxSize()) }
            )
        }

        MediaPlayerBackHandler(state1)
        MediaPlayerBackHandler(state2)
        MediaPlayerBackHandler(state3)
        FullScreenPlayerOverlay(
            playerState = state1,
            url = url1,
            cover = { VideoCover(Modifier.fillMaxSize()) }
        )
        FullScreenPlayerOverlay(
            playerState = state2,
            url = url2,
            cover = { VideoCover(Modifier.fillMaxSize()) }
        )
        FullScreenPlayerOverlay(
            playerState = state3,
            url = url3,
            cover = { VideoCover(Modifier.fillMaxSize()) }
        )
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
